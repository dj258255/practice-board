package io.github.beom.practiceboard.attachment.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//파일 업로드 정보를 위한 DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRequestDTO {
    private String uuid; //클라이언트가 보내는 파일 식별자
    private String fileName;//원본 파일명
    private int ord; //파일 순서

}
