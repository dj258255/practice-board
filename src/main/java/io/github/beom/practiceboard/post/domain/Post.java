package io.github.beom.practiceboard.post.domain;

import io.github.beom.practiceboard.attachment.domain.Attachment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 게시글 도메인 객체
 * 실제 사용자가 작성한 글을 나타냅니다.
 */
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Post {
    
    private Long id;
    private String title;
    private String content;
    private Long authorId; // 작성자 ID
    
    private Long categoryId; // 카테고리 ID 참조
    
    @Builder.Default
    private PostType postType = PostType.NORMAL;
    
    @Setter
    @Builder.Default
    private Set<Attachment> attachments = new HashSet<>();
    
    // 통계 관련 필드들
    @Builder.Default
    private int viewCount = 0;
    
    @Builder.Default
    private int favoriteCount = 0;
    
    @Builder.Default
    private int dislikeCount = 0;
    
    @Builder.Default
    private int commentCount = 0;
    
    // 시간 관련 필드들
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    /**
     * 게시글 조회수 증가
     */
    public void increaseViewCount() {
        this.viewCount++;
    }
    
    /**
     * 댓글 수 증가
     */
    public void increaseCommentCount() {
        this.commentCount++;
    }
    
    /**
     * 댓글 수 감소
     */
    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
    
    /**
     * 좋아요 수 증가
     */
    public void increaseFavoriteCount() {
        this.favoriteCount++;
    }
    
    /**
     * 좋아요 수 감소
     */
    public void decreaseFavoriteCount() {
        if (this.favoriteCount > 0) {
            this.favoriteCount--;
        }
    }
    
    /**
     * 싫어요 수 증가
     */
    public void increaseDislikeCount() {
        this.dislikeCount++;
    }
    
    /**
     * 싫어요 수 감소
     */
    public void decreaseDislikeCount() {
        if (this.dislikeCount > 0) {
            this.dislikeCount--;
        }
    }
    
    /**
     * 게시글이 삭제되었는지 확인
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    /**
     * 첨부파일이 있는지 확인
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }
    
    /**
     * 특정 사용자가 작성한 게시글인지 확인
     */
    public boolean isAuthor(Long userId) {
        return this.authorId != null && this.authorId.equals(userId);
    }
    
    /**
     * 게시글 수정 (제목, 내용만)
     */
    public Post updateContent(String title, String content) {
        return this.toBuilder()
                .title(title)
                .content(content)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시글 공지사항으로 변경
     */
    public Post changeToNotice() {
        return this.toBuilder()
                .postType(PostType.NOTICE)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시글 일반 글로 변경
     */
    public Post changeToNormal() {
        return this.toBuilder()
                .postType(PostType.NORMAL)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}