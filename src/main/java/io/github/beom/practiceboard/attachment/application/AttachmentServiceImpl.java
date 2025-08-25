package io.github.beom.practiceboard.attachment.application;

import io.github.beom.practiceboard.attachment.domain.Attachment;
import io.github.beom.practiceboard.attachment.presentation.AttachmentService;
import io.github.beom.practiceboard.attachment.presentation.dto.response.AttachmentResponseDTO;
import io.github.beom.practiceboard.s3.helper.S3Helper;
import io.github.beom.practiceboard.s3.util.LocalUploader;
import io.github.beom.practiceboard.s3.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.beom.practiceboard.attachment.exception.FileUploadException;
import io.github.beom.practiceboard.attachment.exception.FileNotFoundException;
import io.github.beom.practiceboard.attachment.exception.FileDeleteException;
import io.github.beom.practiceboard.attachment.exception.InvalidFileNameException;

import java.time.LocalDateTime;
import java.util.*;


//파일 업로드/조회/삭제 만 담당함.
//각 도메인별 파일 관리는 해당 도메인 서비스에서 처리하면 됌.
@Service
@RequiredArgsConstructor
@Log4j2
public class AttachmentServiceImpl implements AttachmentService {
    private final LocalUploader localUploader;
    private final S3Uploader s3Uploader;
    private final S3Helper s3Helper;

    @Override
    public List<AttachmentResponseDTO> uploadFiles(List<MultipartFile> files, String domain, Long referenceId) {
        List<AttachmentResponseDTO> uploadedFiles = new ArrayList<>();
        int order = 0;

        for(MultipartFile multipartFile : files){
            List<String> localPaths = localUploader.uploadLocal(multipartFile);
            if(localPaths != null && !localPaths.isEmpty()){
                String originalName = multipartFile.getOriginalFilename();
                String uuid = s3Helper.extractUuidFromUrl(localPaths.get(0));
                //S3 업로드
                List<String> s3Urls = localPaths.stream()
                        .map(path -> domain != null ? s3Uploader.upload(path,domain) : s3Uploader.upload(path))
                        .toList();

                boolean isImage =s3Urls.size()> 1;

                log.debug("파일 업로드 처리 - 파일명: {}, 로컬경로수: {}, S3URL수: {}, 이미지여부: {}", originalName, localPaths.size(), s3Urls.size(), isImage);

                //S3Url 제외 빌드
                AttachmentResponseDTO attachmentResponseDTO = AttachmentResponseDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(isImage)
                        .ord(order++)
                        .fileSize(multipartFile.getSize())
                        .contentType(multipartFile.getContentType())
                        .domain(domain)
                        .referenceId(referenceId)
                        .updatedAt(LocalDateTime.now())
                        .build();
                //S3Url 정보 추가
                String originalUrl = domain != null ?
                        s3Helper.getDomainOriginalUrl(domain,uuid,originalName)
                        : s3Helper.getOriginalUrl(uuid,originalName);
                String thumbnailUrl = isImage ?
                        (domain != null ?
                                s3Helper.getDomainThumbnailUrl(domain, uuid, originalName) :
                                s3Helper.getThumbnailUrl(uuid, originalName)) : null;

                attachmentResponseDTO.setOriginalS3Url(originalUrl);
                attachmentResponseDTO.setThumbnailS3Url(thumbnailUrl);

                log.info("파일 업로드 완료 - UUID: {}, 이미지: {}, 원본URL: {}, 썸네일URL: {}",
                        uuid, isImage, originalUrl, thumbnailUrl);

                uploadedFiles.add(attachmentResponseDTO);
            }
        }
        return uploadedFiles;
    }

    @Override
    public Map<String, Object> getFileInfo(String fileName) {
        try{
            //S3 URL 생성
            String s3Url;
            if(fileName.contains("_")){
                String uuid = fileName.substring(0, fileName.indexOf("_"));
                String originalName = fileName.substring(fileName.indexOf("_")+1);
                s3Url = s3Helper.getOriginalUrl(uuid,originalName);
            } else{
                throw new InvalidFileNameException("파일명에 UUID가 없습니다. 올바른 형식의 파일명을 사용해주세요: " + fileName);
            }
            log.info("생성된 S3 URL: {}", s3Url);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("url", s3Url);
            resultMap.put("fileName", fileName);

            return resultMap;
        } catch (InvalidFileNameException e) {
            throw e;
        } catch (Exception e){
            log.error("파일 정보 조회 실패: {} - {}", fileName, e.getMessage());
            throw new FileNotFoundException("파일 정보 조회 실패", e);
        }
    }

    @Override
    public boolean removeFile(String fileName) {
        try {
            // S3에서 파일 삭제
            s3Uploader.removeS3File(fileName);
            // 이미지인 경우 섬네일 삭제
            if (s3Helper.isImageFile(fileName)) {
                // UUID_파일명 형식에서 UUID와 원본 파일명 추출
                if (fileName.contains("_")) {
                    String uuid = fileName.substring(0, fileName.indexOf('_'));
                    String originalFileName = fileName.substring(fileName.indexOf('_') + 1);
                    String thumbnailFileName = s3Helper.createThumbnailFileName(uuid, originalFileName);
                    s3Uploader.removeS3File(thumbnailFileName);
                } else {
                    // 기존 방식 유지 (하위 호환성)
                    String thumbnailFileName = "s_" + fileName;
                    s3Uploader.removeS3File(thumbnailFileName);
                }
            }

            return true;
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage());
            throw new FileDeleteException("S3 파일 삭제 실패", e);
        }
    }


}
