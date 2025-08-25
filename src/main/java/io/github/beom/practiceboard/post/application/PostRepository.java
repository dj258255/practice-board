package io.github.beom.practiceboard.post.application;

import io.github.beom.practiceboard.post.domain.Post;
import io.github.beom.practiceboard.post.domain.PostType;
import io.github.beom.practiceboard.post.presentation.dto.request.PostPageRequestDTO;
import io.github.beom.practiceboard.post.presentation.dto.response.PostPageResponseDTO;
import io.github.beom.practiceboard.post.presentation.dto.response.PostResponseDTO;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 Repository 인터페이스
 */
public interface PostRepository {
    
    /**
     * 게시글 등록
     */
    Long save(Post post);
    
    /**
     * 게시글 조회
     */
    Optional<Post> findById(Long postId);
    
    /**
     * 게시글 수정
     */
    void update(Post post);
    
    /**
     * 게시글 삭제 (소프트 삭제)
     */
    void delete(Long postId);
    
    /**
     * 게시글 물리 삭제
     */
    void deletePhysically(Long postId);
    
    /**
     * 카테고리별 게시글 목록 조회 (페이징)
     */
    PostPageResponseDTO<PostResponseDTO> findByCategory(Long categoryId, PostPageRequestDTO pageRequest);
    
    /**
     * 게시글 타입별 목록 조회 (페이징)
     */
    PostPageResponseDTO<PostResponseDTO> findByType(PostType postType, PostPageRequestDTO pageRequest);
    
    /**
     * 작성자별 게시글 목록 조회 (페이징)
     */
    PostPageResponseDTO<PostResponseDTO> findByAuthor(Long authorId, PostPageRequestDTO pageRequest);
    
    /**
     * 전체 게시글 목록 조회 (페이징)
     */
    PostPageResponseDTO<PostResponseDTO> findAll(PostPageRequestDTO pageRequest);
    
    /**
     * 게시글 검색 (제목, 내용, 작성자)
     */
    PostPageResponseDTO<PostResponseDTO> search(String keyword, PostPageRequestDTO pageRequest);
    
    /**
     * 인기 게시글 목록 조회 (조회수, 좋아요 기준)
     */
    List<PostResponseDTO> findPopularPosts(int limit);
    
    /**
     * 최근 게시글 목록 조회
     */
    List<PostResponseDTO> findRecentPosts(int limit);
    
    /**
     * 공지사항 목록 조회
     */
    List<PostResponseDTO> findNotices();
    
    /**
     * 카테고리별 게시글 수 조회
     */
    long countByCategory(Long categoryId);
    
    /**
     * 작성자별 게시글 수 조회
     */
    long countByAuthor(Long authorId);
    
    /**
     * 게시글 존재 여부 확인
     */
    boolean existsById(Long postId);
    
    /**
     * 게시글 조회수 증가
     */
    void increaseViewCount(Long postId);
    
    /**
     * 게시글 댓글 수 업데이트
     */
    void updateCommentCount(Long postId, int commentCount);
    
    /**
     * 게시글 좋아요 수 업데이트
     */
    void updateFavoriteCount(Long postId, int favoriteCount);
}