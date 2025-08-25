package io.github.beom.practiceboard.post.presentation.dto.response;

import io.github.beom.practiceboard.attachment.presentation.dto.response.AttachmentResponseDTO;
import io.github.beom.practiceboard.post.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 목록 조회용 응답 DTO
 * 게시글 목록에서 필요한 모든 정보를 포함합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostListAllResponseDTO {

    private Long id;

    private String title;

    private String writer;

    private LocalDateTime createdAt;

    private Long replyCount;

    private Long categoryId;

    private PostType postType;

    private long view;

    private long favorite;

    private long dislike;

    private List<AttachmentResponseDTO> postFiles;
}