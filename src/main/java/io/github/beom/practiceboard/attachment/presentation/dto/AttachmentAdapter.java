package io.github.beom.practiceboard.attachment.presentation.dto;

import io.github.beom.practiceboard.attachment.domain.Attachment;
import io.github.beom.practiceboard.attachment.presentation.dto.request.AttachmentRequestDTO;
import io.github.beom.practiceboard.attachment.presentation.dto.response.AttachmentResponseDTO;
import io.github.beom.practiceboard.s3.helper.S3Helper;
import io.github.beom.practiceboard.s3.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

//Attachment 도메인 <-> DTO 변환  담당하는 어댑터
@Component
@RequiredArgsConstructor
@Log4j2
public class AttachmentAdapter {
    private final S3Uploader s3Uploader;
    private final S3Helper s3Helper;
    
    //Attachment 도메인 -> AttachmentResponseDTO로 변환
    public AttachmentResponseDTO toDto(Attachment attachment){
        if(attachment == null) return null;

        AttachmentResponseDTO attachmentResponseDTO = AttachmentResponseDTO.builder()
                .uuid(attachment.getUuid())
                .fileName(attachment.getFileName())
                .img(attachment.isImg())
                .ord(attachment.getOrd())
                .fileSize(attachment.getFileSize())
                .contentType(attachment.getContentType())
                .domain(attachment.getDomain())
                .referenceId(attachment.getReferenceId())
                .updatedAt(attachment.getUpdatedAt())
                .createdBy(attachment.getCreatedBy())
                .build();

        //S3Url 설정 도메인이 있으면
        if(attachment.getDomain() != null && !attachment.getDomain().isEmpty()){
            //도메인별 url 생성
            attachmentResponseDTO.setOriginalS3Url(s3Helper.getDomainOriginalUrl(attachment.getDomain(),attachment.getUuid(),attachment.getFileName()));
            if(attachment.isImg()){
                attachmentResponseDTO.setThumbnailS3Url(s3Helper.getDomainThumbnailUrl(attachment.getDomain(),attachment.getUuid(),attachment.getFileName()));
            }
        } else{
            //기본 url 생성
            attachmentResponseDTO.setOriginalS3Url(s3Helper.getOriginalUrl(attachment.getUuid(),attachment.getFileName()));

            if(attachment.isImg()){
                attachmentResponseDTO.setThumbnailS3Url(s3Helper.getThumbnailUrl(attachment.getUuid(),attachment.getFileName()));
            }
        }
        return attachmentResponseDTO;
    }

    //AttachmentRequestDTO를 Attachment로 변환
    public Attachment toAttachment(AttachmentRequestDTO attachmentRequestDTO){
        if(attachmentRequestDTO == null) return null;

        return Attachment.builder()
                .uuid(attachmentRequestDTO.getUuid())
                .fileName(attachmentRequestDTO.getFileName())
                .ord(attachmentRequestDTO.getOrd())
                .build();
    }
}