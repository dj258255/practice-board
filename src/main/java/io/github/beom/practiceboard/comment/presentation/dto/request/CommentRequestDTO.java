package io.github.beom.practiceboard.comment.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 등록/수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId; // Post와 일치시킴

    @NotNull(message = "작성자 ID는 필수입니다.")
    private Long authorId; // 작성자 ID

    @NotEmpty(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 255, message = "댓글 내용은 1~255자 이내여야 합니다.")
    private String content; // Comment 도메인과 일치

    @NotNull(message = "게시판 ID는 필수입니다.")
    private Long boardId;

    private Long parentReplyId; // 부모 댓글 ID (null이면 최상위 댓글)

    @Builder.Default
    private int depth = 0; // 댓글 깊이 (0: 최상위 댓글, 1: 대댓글)

    /**
     * 대댓글인지 확인
     * @return 대댓글 여부
     */
    public boolean isChildReply() {
        return parentReplyId != null;
    }

    /**
     * 최대 깊이에 도달했는지 확인 (최대 1계층)
     * @return 최대 깊이 도달 여부
     */
    public boolean isMaxDepth() {
        return depth >= 1; // 최대 1계층까지만 허용
    }
}