package io.github.beom.practiceboard.s3.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import io.github.beom.practiceboard.s3.helper.S3Helper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

//디자인패턴 퍼사드 적용
@Component
@RequiredArgsConstructor
@Log4j2
public class S3Uploader {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static")
    private String region;

    //로컬 파일을 S3로 업로드
    //filePath는 /Users/admin/Desktop/abc123_profile.png 이런느낌
    public String upload(String filePath) throws RuntimeException{
        File targetFile = new File(filePath);
        // 예외
        try{
            String s3Key = targetFile.getName();
            //S3 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            s3Client.putObject(putObjectRequest, Paths.get(targetFile.getPath()));
            //업로드 후 로컬 파일 삭제
            removeOriginalFile(targetFile);
            // URL 반환
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, s3Key);

        } catch (Exception e){
            log.error("S3 업로드 실패 : {}" , e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //로컬 파일을 S3의 특정 디렉토리로 업로드
    public String upload(String filePath, String s3Directory) throws RuntimeException{
        File targetFile = new File(filePath);
        try{ //s3로 업로드
        String s3Key = s3Directory + "/" + targetFile.getName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3Client.putObject(putObjectRequest, Paths.get(targetFile.getPath()));
        //업로드 후 로컬 파일 삭제
            removeOriginalFile(targetFile);
            //url 반환
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, s3Key);
        } catch (Exception e){
            log.error("S3 업로드 실패 : {}" , e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //Presigned URL 생성 (UUID와 파일명 분리)
    public String generatePresignedUrl(String s3Directory, String uuid, String fileName, Duration duration){
        try{
            String s3Key = String.format("%s/%s_%s" , s3Directory , uuid , fileName);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .putObjectRequest(objectRequest)
                    .build();
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e){
            log.error("Presigned URL 생성 실패 : {}" , e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    //S3로 업로드 후 원본 파일 삭제
    private void removeOriginalFile(File targetFile){
        if(targetFile.exists() && targetFile.delete()){
            log.info("로컬 파일 삭제 성공 : {}", targetFile.getName());
        } else {
            log.info("로컬 파일 삭제 실패 : {}", targetFile.getName());
        }
    }
    //파일 삭제 메서드 ===============================================================================

    //S3에서 파일 삭제 , fileName = UUID랑 전부다 합친 파일네임
    public void removeS3File(String fileName){
        try{
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공 : {}", fileName);
        } catch(Exception e){
            log.info("S3 파일 삭제 실패 : {} - {}", fileName, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
