package io.github.beom.practiceboard.comment.infrastructure;

import io.github.beom.practiceboard.global.base.BaseAllEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 댓글 JPA 엔티티
 * 댓글 데이터베이스 테이블과 매핑되는 엔티티입니다.
 */
@Entity
@Table(name = "comment", indexes = {
        @Index(name = "idx_comment_board_id", columnList = "board_id"),
        @Index(name = "idx_comment_parent_id", columnList = "parent_id"),
        @Index(name = "idx_comment_depth", columnList = "depth"),
        @Index(name = "idx_comment_created_at", columnList = "created_at")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parentComment"})
public class CommentJpaEntity extends BaseAllEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "reply_text", nullable = false, length = 255)
    private String replyText;
    
    @Column(name = "board_id", nullable = false)
    private Long boardId;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentJpaEntity parentComment; // 부모 댓글 참조
    
    @Column(name = "depth", nullable = false)
    @Builder.Default
    private int depth = 0; // 댓글 깊이 (0: 최상위 댓글, 1: 대댓글)




    /**
     * 대댓글인지 확인
     * @return 대댓글 여부
     */
    public boolean isChildComment() {
        return parentComment != null;
    }

    /**
     * 최상위 댓글인지 확인
     * @return 최상위 댓글 여부
     */
    public boolean isRootComment() {
        return parentComment == null && depth == 0;
    }

    /**
     * 최대 깊이에 도달했는지 확인
     * @return 최대 깊이 도달 여부
     */
    public boolean isMaxDepth() {
        return depth >= 1; // 최대 1계층까지만 허용
    }

    
    /**
     * replayer 필드 getter (호환성 유지)
     */
    public String getReplayer() {
        return this.authorId != null ? this.authorId.toString() : null;
    }
}