package io.github.beom.practiceboard.attachment.presentation;

import io.github.beom.practiceboard.attachment.presentation.dto.AttachmentAdapter;
import io.github.beom.practiceboard.attachment.presentation.dto.response.AttachmentResponseDTO;
import io.github.beom.practiceboard.attachment.presentation.dto.request.AttachmentUploadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attachment")
@RequiredArgsConstructor
@Log4j2
public class AttachmentController {
    private final AttachmentService attachmentService;

    //파일 업로드 post방식으로 등록.
    @PostMapping("/upload")
    public ResponseEntity<List<AttachmentResponseDTO>> upload(@ModelAttribute AttachmentUploadDTO attachmentUploadDTO){
        log.info("파일 업로드 요청 - 도메인: {}, 참조ID: {}, 파일 수: {}",
                attachmentUploadDTO.getDomain(), attachmentUploadDTO.getReferenceId(),
                (attachmentUploadDTO.getFiles() != null) ? attachmentUploadDTO.getFiles().size() : 0);

        if (attachmentUploadDTO.getFiles() == null || attachmentUploadDTO.getFiles().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<AttachmentResponseDTO> uploadedFiles = attachmentService.uploadFiles(
                attachmentUploadDTO.getFiles(),
                attachmentUploadDTO.getDomain(),
                attachmentUploadDTO.getReferenceId());

        if (uploadedFiles.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(uploadedFiles);
    }

    //파일 정보와 S3URL을 JSON으로 반환
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Map<String,Object>> getFileInfo(@PathVariable String fileName){
        log.info("파일 정보 조회 요청 : {}" , fileName);

        Map<String,Object> fileInfo = attachmentService.getFileInfo(fileName);
        return ResponseEntity.ok(fileInfo);
    }

    //파일 삭제
    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<Map<String, Boolean>> removeFile(@PathVariable String fileName){
        log.info("파일 삭제 요청: {}", fileName);

        boolean removed = attachmentService.removeFile(fileName);

        Map<String, Boolean> resultMap = new HashMap<>();
        resultMap.put("result", removed);

        return ResponseEntity.ok(resultMap);
    }
}
