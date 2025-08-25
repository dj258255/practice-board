package io.github.beom.practiceboard.comment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    private Long id;
    
    private Long postId; // 게시글 ID 추가
    
    private Long boardId;
    
    private String content; // replyText -> content로 변경
    
    private Long authorId; // replayer -> authorId로 변경
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime updatedAt;
    
    
    private Long parentReplyId; // 부모 댓글 ID (null이면 최상위 댓글)
    
    @Builder.Default
    private int depth = 0; // 댓글 깊이 (0: 최상위 댓글, 1: 대댓글)
    
    // 대댓글 목록 (조회 시 사용)
    @Builder.Default
    private List<CommentResponseDTO> children = new ArrayList<>();
    
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
    
    /**
     * 대댓글이 있는지 확인
     * @return 대댓글 존재 여부
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    /**
     * 대댓글 추가
     * @param child 대댓글
     */
    public void addChild(CommentResponseDTO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }
    
    /**
     * 대댓글 개수 조회
     * @return 대댓글 개수
     */
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }
}