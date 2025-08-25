package io.github.beom.practiceboard.board.presentation.dto.response;

import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시판 응답 DTO
 */
@Schema(description = "게시판 응답 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDTO {
    
    @Schema(description = "게시판 ID", example = "1")
    private Long id;
    
    @Schema(description = "게시판 이름", example = "자유게시판")
    private String name;
    
    @Schema(description = "게시판 설명", example = "자유롭게 글을 올리는 게시판입니다.")
    private String description;
    
    @Schema(description = "URL 식별자", example = "free-board")
    private String slug;
    
    @Schema(description = "게시판 타입", example = "NORMAL")
    private BoardType boardType;
    
    @Schema(description = "게시판 상태", example = "ACTIVE")
    private BoardStatus status;
    
    // 게시판 설정
    @Schema(description = "익명 게시 허용 여부", example = "false")
    private boolean allowAnonymous;
    
    @Schema(description = "게시글 승인 필요 여부", example = "false")
    private boolean requireApproval;
    
    @Schema(description = "첨부파일 허용 여부", example = "true")
    private boolean allowAttachment;
    
    @Schema(description = "최대 첨부파일 크기 (MB)", example = "10")
    private int maxAttachmentSize;
    
    // 통계 정보
    @Schema(description = "게시글 수", example = "150")
    private int postCount;
    
    @Schema(description = "카테고리 수", example = "5")
    private int categoryCount;
    
    // 관리자 정보
    @Schema(description = "게시판 관리자 ID", example = "1")
    private Long managerId;
    
    @Schema(description = "관리자 이름", example = "관리자")
    private String managerName;
    
    // 시간 정보
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
    
    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;
    
    // 추가 정보
    @Schema(description = "현재 사용자가 관리 권한이 있는지", example = "false")
    private boolean canManage;
    
    @Schema(description = "게시판이 활성 상태인지", example = "true")
    private boolean isActive;
    
    @Schema(description = "게시판이 삭제된 상태인지", example = "false")
    private boolean isDeleted;
    
    /**
     * 게시판이 활성 상태인지 확인
     */
    public boolean isActive() {
        return status == BoardStatus.ACTIVE && deletedAt == null;
    }
    
    /**
     * 게시판이 삭제된 상태인지 확인
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}