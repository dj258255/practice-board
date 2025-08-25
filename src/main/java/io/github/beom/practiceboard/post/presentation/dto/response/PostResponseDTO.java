package io.github.beom.practiceboard.post.presentation.dto.response;

import io.github.beom.practiceboard.attachment.domain.Attachment;
import io.github.beom.practiceboard.post.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 게시글 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Long authorId;
    private Long boardId;
    private String boardName; // 게시판 이름
    private Long categoryId;
    private String categoryName; // 카테고리 이름
    private PostType postType;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private boolean isPinned;
    private boolean isFeatured;
    private Set<Attachment> attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // 작성자 정보
    private String authorProfileImage;
    private String authorEmail;
    
    // 추가 정보
    private boolean isLikedByCurrentUser; // 현재 사용자가 좋아요 했는지
    private boolean isFavoritedByCurrentUser; // 현재 사용자가 즐겨찾기 했는지
    private boolean canEdit; // 편집 권한
    private boolean canDelete; // 삭제 권한
    
    // 게시글 요약 정보 (리스트에서 사용)
    private String contentSummary; // 내용 요약 (첫 100자 등)
    
    /**
     * 내용 요약 생성
     */
    public String getContentSummary() {
        if (contentSummary == null && content != null) {
            // HTML 태그 제거 후 100자 제한
            String plainText = content.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
            contentSummary = plainText.length() > 100 ? 
                plainText.substring(0, 100) + "..." : plainText;
        }
        return contentSummary;
    }
    
    /**
     * 첨부파일 개수
     */
    public int getAttachmentCount() {
        return attachments != null ? attachments.size() : 0;
    }
    
    /**
     * 이미지 첨부파일 개수
     */
    public long getImageCount() {
        return attachments != null ? 
            attachments.stream().filter(Attachment::isImg).count() : 0;
    }
}