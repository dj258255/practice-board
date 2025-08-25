package io.github.beom.practiceboard.post.presentation.dto.request;

import io.github.beom.practiceboard.post.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 게시글 생성/수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Long authorId;
    private Long boardId;
    private Long categoryId;
    private PostType postType;
    private boolean isPinned;
    private boolean isFeatured;
    private List<String> fileNames; // 첨부파일 이름 목록
}