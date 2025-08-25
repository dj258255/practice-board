package io.github.beom.practiceboard.post.application;

import io.github.beom.practiceboard.post.domain.Post;
import io.github.beom.practiceboard.post.domain.PostType;
import io.github.beom.practiceboard.post.presentation.dto.response.PostPageResponseDTO;
import io.github.beom.practiceboard.post.presentation.dto.response.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Post 검색 Repository 인터페이스
 * 복잡한 검색 조건과 동적 쿼리를 위한 인터페이스
 */
public interface PostSearchRepository {

    /**
     * 다중 조건으로 게시글 검색
     * @param types 검색 타입 배열 (t: 제목, c: 내용, a: 작성자)
     * @param keyword 검색 키워드
     * @param boardId 게시판 ID (필터링용)
     * @param categoryId 카테고리 ID (필터링용)
     * @param authorId 작성자 ID (필터링용)
     * @param postType 게시글 타입 (필터링용)
     * @param pinnedOnly 고정글만 조회 여부
     * @param featuredOnly 추천글만 조회 여부
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<PostResponseDTO> searchPosts(String[] types, 
                                     String keyword,
                                     Long boardId,
                                     Long categoryId, 
                                     Long authorId,
                                     PostType postType,
                                     Boolean pinnedOnly,
                                     Boolean featuredOnly,
                                     Pageable pageable);

    /**
     * 게시글과 댓글 수를 함께 조회하는 검색
     * @param types 검색 타입 배열
     * @param keyword 검색 키워드
     * @param boardId 게시판 ID
     * @param categoryId 카테고리 ID
     * @param authorId 작성자 ID
     * @param postType 게시글 타입
     * @param pageable 페이징 정보
     * @return 게시글과 댓글 수 정보
     */
    PostPageResponseDTO<PostResponseDTO> searchWithCommentCount(String[] types,
                                                               String keyword,
                                                               Long boardId,
                                                               Long categoryId,
                                                               Long authorId,
                                                               PostType postType,
                                                               Pageable pageable);

    /**
     * 인기 게시글 검색 (좋아요, 조회수 기준)
     * @param boardId 게시판 ID
     * @param categoryId 카테고리 ID
     * @param period 기간 (일 단위)
     * @param pageable 페이징 정보
     * @return 인기 게시글 목록
     */
    Page<PostResponseDTO> searchPopularPosts(Long boardId, 
                                           Long categoryId,
                                           int period,
                                           Pageable pageable);

    /**
     * 관련 게시글 검색 (태그, 카테고리 기반)
     * @param postId 기준 게시글 ID
     * @param limit 결과 개수 제한
     * @return 관련 게시글 목록
     */
    List<PostResponseDTO> searchRelatedPosts(Long postId, int limit);

    /**
     * 통계를 포함한 게시글 검색
     * @param types 검색 타입 배열
     * @param keyword 검색 키워드
     * @param boardId 게시판 ID
     * @param categoryId 카테고리 ID
     * @param pageable 페이징 정보
     * @return 통계 정보가 포함된 검색 결과
     */
    PostPageResponseDTO<PostResponseDTO> searchWithStatistics(String[] types,
                                                             String keyword,
                                                             Long boardId,
                                                             Long categoryId,
                                                             Pageable pageable);
}