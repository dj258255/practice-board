package io.github.beom.practiceboard.post.infrastructure;

import io.github.beom.practiceboard.post.domain.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Post JPA Repository
 */
public interface PostJpaRepository extends JpaRepository<PostJpaEntity, Long> {

    /**
     * 삭제되지 않은 게시글 조회
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.deletedAt IS NULL AND p.id = :id")
    Optional<PostJpaEntity> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 특정 게시판의 게시글 목록 조회 (페이징)
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.boardId = :boardId AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByBoardIdAndNotDeleted(@Param("boardId") Long boardId, Pageable pageable);

    /**
     * 특정 카테고리의 게시글 목록 조회 (페이징)
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.categoryId = :categoryId AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByCategoryIdAndNotDeleted(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * 특정 작성자의 게시글 목록 조회 (페이징)
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.authorId = :authorId AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByAuthorIdAndNotDeleted(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * 특정 타입의 게시글 목록 조회 (페이징)
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.postType = :postType AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByPostTypeAndNotDeleted(@Param("postType") PostType postType, Pageable pageable);

    /**
     * 핀된 게시글 목록 조회
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.isPinned = true AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<PostJpaEntity> findPinnedPosts();

    /**
     * 추천 게시글 목록 조회
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.isFeatured = true AND p.deletedAt IS NULL ORDER BY p.likeCount DESC")
    List<PostJpaEntity> findFeaturedPosts(Pageable pageable);

    /**
     * 인기 게시글 조회 (좋아요 수 기준)
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.deletedAt IS NULL ORDER BY p.likeCount DESC")
    List<PostJpaEntity> findPopularPosts(Pageable pageable);

    /**
     * 최근 게시글 조회
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<PostJpaEntity> findRecentPosts(Pageable pageable);

    /**
     * 특정 기간 내 게시글 조회
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.deletedAt IS NULL")
    List<PostJpaEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * 제목으로 검색
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.title LIKE %:keyword% AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 내용으로 검색
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.content LIKE %:keyword% AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 작성자명으로 검색
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE p.writer LIKE %:keyword% AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByWriterContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 통합 검색 (제목 + 내용)
     */
    @Query("SELECT p FROM PostJpaEntity p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.deletedAt IS NULL")
    Page<PostJpaEntity> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 게시판별 게시글 수 카운트
     */
    @Query("SELECT COUNT(p) FROM PostJpaEntity p WHERE p.boardId = :boardId AND p.deletedAt IS NULL")
    long countByBoardId(@Param("boardId") Long boardId);

    /**
     * 카테고리별 게시글 수 카운트
     */
    @Query("SELECT COUNT(p) FROM PostJpaEntity p WHERE p.categoryId = :categoryId AND p.deletedAt IS NULL")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 작성자별 게시글 수 카운트
     */
    @Query("SELECT COUNT(p) FROM PostJpaEntity p WHERE p.authorId = :authorId AND p.deletedAt IS NULL")
    long countByAuthorId(@Param("authorId") Long authorId);
}