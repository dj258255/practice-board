package io.github.beom.practiceboard.attachment.presentation.dto.response;

//파일 업로드 결과

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponseDTO {
    //파일 고유 ID 반환
    private String uuid;
    private String fileName;
    private int ord;
    private boolean img;
    private Long fileSize;
    private String contentType;
    private String domain;
    private Long referenceId;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private String originalS3Url;
    private String thumbnailS3Url;

    //썸네일 파일으 S3 URL 반환(이미지일 때만) thumbnailUrl이 설정되어 있으면 그 값을 사용
    public String getThumbnailUrl() {
        if (!img) return null;
        return thumbnailS3Url;
    }

    //파일 이미지 인지 확인
    public boolean isImageFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension);
    }

    //파일 확장자를 반환
    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    //파일 크기를 MB 단위로 변환
    public double getFileSizeInMB(){
        if(fileSize == null) return 0.0;
        return fileSize / 1024.0 / 1024.0;
    }

}
