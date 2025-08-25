package io.github.beom.practiceboard.comment.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)  // toBuilder 추가
@RequiredArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id; //댓글 id
    private Long postId; //게시글 id
    private Long boardId; //게시판 id - 추가
    private String content; // replyText -> content로 변경
    private Long authorId;
    
    // 시간 관련 필드 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    @Builder.Default
    private Long parentReplyId = null; // 부모 댓글 ID (null이면 최상위 댓글)
    
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