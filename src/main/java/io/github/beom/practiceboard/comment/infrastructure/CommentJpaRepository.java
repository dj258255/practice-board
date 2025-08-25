package io.github.beom.practiceboard.comment.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 댓글 JPA 리포지토리
 * 댓글 데이터베이스 접근을 위한 JPA 리포지토리입니다.
 */
public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, Long> {
    
    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     * 삭제되지 않은 댓글만 조회
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId ORDER BY c.createdAt ASC")
    Page<CommentJpaEntity> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);
    
    /**
     * 특정 게시글의 최상위 댓글만 조회 (페이징)
     * 삭제되지 않은 댓글만 조회
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    Page<CommentJpaEntity> findRootCommentsByBoardId(@Param("boardId") Long boardId, Pageable pageable);
    
    /**
     * 특정 부모 댓글의 대댓글 목록 조회
     * 시간순 정렬
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.parentComment.id = :parentId ORDER BY c.createdAt ASC")
    List<CommentJpaEntity> findByParentCommentId(@Param("parentId") Long parentId);

    /**
     * 특정 게시글의 모든 댓글 조회
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId ORDER BY c.createdAt ASC")
    List<CommentJpaEntity> findAllByBoardId(@Param("boardId") Long boardId);
    
    /**
     * 특정 게시글의 댓글 개수 조회
     */
    @Query("SELECT COUNT(c) FROM CommentJpaEntity c WHERE c.boardId = :boardId")
    long countByBoardId(@Param("boardId") Long boardId);
    
    /**
     * 특정 부모 댓글의 대댓글 개수 조회
     */
    @Query("SELECT COUNT(c) FROM CommentJpaEntity c WHERE c.parentComment.id = :parentId")
    long countByParentCommentId(@Param("parentId") Long parentId);
    
    /**
     * 특정 댓글이 대댓글을 가지고 있는지 확인
     */
    @Query("SELECT COUNT(c) > 0 FROM CommentJpaEntity c WHERE c.parentComment.id = :parentId")
    boolean hasChildComments(@Param("parentId") Long parentId);
    
    /**
     * 특정 게시글의 모든 댓글 삭제 (물리적 삭제)
     */
    @Modifying
    @Query("DELETE FROM CommentJpaEntity c WHERE c.boardId = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);
    
    /**
     * 특정 댓글의 모든 대댓글 삭제 (물리적 삭제)
     */
    @Modifying
    @Query("DELETE FROM CommentJpaEntity c WHERE c.parentComment.id = :parentId")
    void deleteByParentCommentId(@Param("parentId") Long parentId);
    
    /**
     * 게시글별 댓글 검색 (내용 기준)
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId AND c.replyText LIKE %:keyword% ORDER BY c.createdAt ASC")
    Page<CommentJpaEntity> findByBoardIdAndReplyTextContaining(@Param("boardId") Long boardId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 게시글별 댓글 검색 (작성자 기준)
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId AND CAST(c.authorId AS string) LIKE %:keyword% ORDER BY c.createdAt ASC")
    Page<CommentJpaEntity> findByBoardIdAndReplayerContaining(@Param("boardId") Long boardId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 게시글별 댓글 검색 (내용 + 작성자 기준)
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId AND (c.replyText LIKE %:keyword% OR CAST(c.authorId AS string) LIKE %:keyword%) ORDER BY c.createdAt ASC")
    Page<CommentJpaEntity> findByBoardIdAndContentOrReplayer(@Param("boardId") Long boardId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 특정 깊이의 댓글만 조회
     */
    @Query("SELECT c FROM CommentJpaEntity c WHERE c.boardId = :boardId AND c.depth = :depth ORDER BY c.createdAt ASC")
    Page<CommentJpaEntity> findByBoardIdAndDepth(@Param("boardId") Long boardId, @Param("depth") int depth, Pageable pageable);
}