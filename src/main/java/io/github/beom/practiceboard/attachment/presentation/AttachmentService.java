package io.github.beom.practiceboard.attachment.presentation;

import io.github.beom.practiceboard.attachment.domain.Attachment;
import io.github.beom.practiceboard.attachment.presentation.dto.response.AttachmentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

//파일 업로드/조회/삭제만 담당. 각 도메인 별 파일관리는 해당 도메인 서비스에서 처리하면 됌.
public interface AttachmentService {
    //파일업로드 처리
    //files  <- 업로드한 파일 목록
    //domain <-파일이 속한 도메인(Board, profile 등)
    //referenceId- 참조ID 게시글 ID나 유저 ID등
    //반환 :업로드된 파일 정보 모록
    List<AttachmentResponseDTO> uploadFiles(List<MultipartFile> files, String domain, Long referenceId);

    //파일 정보 조회
    //fileName = 파일명(UUID_원본파일명)
    //반환 파일정보와 url 포함된 Map
    Map<String,Object> getFileInfo(String fileName);

    //파일 삭제
    //fileName- 삭제할 파일명
    //반환 : 삭제 성공 여부
    boolean removeFile(String fileName);

}
