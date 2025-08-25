package io.github.beom.practiceboard.s3.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class LocalUploader {

    @Value("${s3.upload.path}")
    private String uploadPath;

    //만약 디렉토리가 없으면 생성

    @PostConstruct
    public void init(){
        log.info("================== 업로드 경로 설정 확인 ==================");
        log.info("업로드 경로 : {}", uploadPath);

        //디렉토리가 없으면 생성
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()){
            boolean created = uploadDir.mkdirs();
            if(created){
                log.info("업로드 디렉토리 생성 완료 : {}", uploadPath);
            }else{
                log.info("업로드 디렉토리 생성 실패 : {}" , uploadPath);
            }
        } else{
            log.info("업로드 디렉토리 존재 확인 : {}" , uploadPath);
        }
    }

    // 1. MultipartFile 타입의 객체를 받아서 실제 로컬폴더에 파일을 저장.
    // 2. 이미지 파일의 경우엔 썸네일생성 및 uuid로 이름 변경
    // 2-1. 일반파일일 경우 uuid로 이름만 변경
    // 3. uploadLocal()의 리턴 값은 UUID 값이 붙은 실제 업로드된 파일의 절대 경로
    // 4. 만일 이미지 파일이 업로드 되면 {원본 파일의 경로, 섬네일 파일의 경로} 2개가 List로 반환
    public List<String> uploadLocal(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.isEmpty()){
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + "_" + multipartFile.getOriginalFilename();
        Path savePath = Paths.get(uploadPath, saveFileName);
        List<String> savePathList = new ArrayList<>();
        try{
            multipartFile.transferTo(savePath);
            savePathList.add(savePath.toFile().getAbsolutePath()); //객체의 절대 경로 추가
            if(Files.probeContentType(savePath).equals("image")){ //만약 이미지면 섬네일 생성
                File thumbFile = new File(uploadPath, "s_" + saveFileName);
                savePathList.add(thumbFile.getAbsolutePath());
                Thumbnailator.createThumbnail(savePath.toFile(), thumbFile,200,200);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return savePathList;
    }
}
