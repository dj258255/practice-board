package io.github.beom.practiceboard.board.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게시판 카테고리 JPA 리포지토리 인터페이스
 */
public interface BoardCategoryJpaRepository extends JpaRepository<BoardCategoryJpaEntity, Long> {

    /**
     * 게시판 ID로 카테고리 목록 조회
     * @param boardId 게시판 ID
     * @return 해당 게시판의 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByBoardId(Long boardId);

    /**
     * 부모 카테고리 ID로 하위 카테고리 목록 조회
     * @param parentId 부모 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByParentId(Long parentId);

    /**
     * 루트 카테고리 목록 조회 (부모가 없는 카테고리)
     * @param boardId 게시판 ID
     * @return 루트 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByBoardIdAndParentIdIsNull(Long boardId);

    /**
     * 활성화된 카테고리 목록 조회
     * @param boardId 게시판 ID
     * @return 활성화된 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByBoardIdAndIsActiveTrue(Long boardId);

    /**
     * 카테고리 이름으로 검색
     * @param boardId 게시판 ID
     * @param categoryName 카테고리 이름 (부분 일치)
     * @return 검색된 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByBoardIdAndCategoryNameContaining(Long boardId, String categoryName);

    /**
     * 특정 게시판의 카테고리 수 조회
     * @param boardId 게시판 ID
     * @return 카테고리 수
     */
    long countByBoardId(Long boardId);

    /**
     * 특정 부모 카테고리 아래의 하위 카테고리 수 조회
     * @param parentId 부모 카테고리 ID
     * @return 하위 카테고리 수
     */
    long countByParentId(Long parentId);

    /**
     * 특정 게시판의 모든 카테고리 삭제
     * @param boardId 게시판 ID
     */
    void deleteByBoardId(Long boardId);

    /**
     * 특정 부모 카테고리 아래의 모든 하위 카테고리 조회 (정렬 순서 적용)
     * @param parentId 부모 카테고리 ID
     * @return 정렬된 하위 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByParentIdOrderBySortOrderAsc(Long parentId);

    /**
     * 특정 게시판의 모든 루트 카테고리 조회 (정렬 순서 적용)
     * @param boardId 게시판 ID
     * @return 정렬된 루트 카테고리 목록
     */
    List<BoardCategoryJpaEntity> findByBoardIdAndParentIdIsNullOrderBySortOrderAsc(Long boardId);

    /**
     * 특정 게시판의 특정 카테고리 이름이 이미 존재하는지 확인
     * @param boardId 게시판 ID
     * @param categoryName 카테고리 이름
     * @return 존재 여부
     */
    boolean existsByBoardIdAndCategoryName(Long boardId, String categoryName);
}