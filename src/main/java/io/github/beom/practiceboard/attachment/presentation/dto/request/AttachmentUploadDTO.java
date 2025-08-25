package io.github.beom.practiceboard.attachment.presentation.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//파일 업로드 요청을 위한 DTO
//컨트롤러 -> 파라미터를 멀티파일로 지정하면 간단한 파일 업로드 가능
//스웨거에서 테스트하기 불편하니 별도의 DTO로 선언
@Data
public class AttachmentUploadDTO {
    private List<MultipartFile> files;
    private String domain;
    private Long referenceId;
}
