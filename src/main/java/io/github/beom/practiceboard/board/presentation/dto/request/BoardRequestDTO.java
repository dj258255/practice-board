package io.github.beom.practiceboard.board.presentation.dto.request;

import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시판 생성/수정 요청 DTO
 */
@Schema(description = "게시판 생성/수정 요청")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDTO {
    
    @Schema(description = "게시판 ID (수정시에만 사용)", example = "1")
    private Long id;
    
    @Schema(description = "게시판 이름", example = "자유게시판", required = true)
    private String name;
    
    @Schema(description = "게시판 설명", example = "자유롭게 글을 올리는 게시판입니다.")
    private String description;
    
    @Schema(description = "URL 식별자 (영문, 숫자, 하이픈만 가능)", example = "free-board", required = true)
    private String slug;
    
    @Schema(description = "게시판 타입", example = "NORMAL")
    @Builder.Default
    private BoardType boardType = BoardType.NORMAL;
    
    @Schema(description = "게시판 상태", example = "ACTIVE")
    @Builder.Default
    private BoardStatus status = BoardStatus.ACTIVE;
    
    // 게시판 설정
    @Schema(description = "익명 게시 허용 여부", example = "false")
    @Builder.Default
    private boolean allowAnonymous = false;
    
    @Schema(description = "게시글 승인 필요 여부", example = "false")
    @Builder.Default
    private boolean requireApproval = false;
    
    @Schema(description = "첨부파일 허용 여부", example = "true")
    @Builder.Default
    private boolean allowAttachment = true;
    
    @Schema(description = "최대 첨부파일 크기 (MB)", example = "10")
    @Builder.Default
    private int maxAttachmentSize = 10;
    
    @Schema(description = "게시판 관리자 ID", example = "1")
    private Long managerId;
}
