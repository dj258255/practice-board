package io.github.beom.practiceboard.board.infrastructure;

import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게시판 JPA Repository
 * 게시판(Board) 엔티티에 대한 데이터 접근을 담당합니다.
 */
public interface BoardJpaRepository extends JpaRepository<BoardJpaEntity, Long> {

    /**
     * ID로 삭제되지 않은 게시판 조회
     */
    @Query("select b from BoardJpaEntity b where b.id = :id and b.deletedAt is null")
    Optional<BoardJpaEntity> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    /**
     * 게시판 상태별 조회 (삭제되지 않은 것만)
     */
    List<BoardJpaEntity> findByStatusAndDeletedAtIsNull(BoardStatus status);

    /**
     * 게시판 타입별 조회 (삭제되지 않은 것만)
     */
    List<BoardJpaEntity> findByBoardTypeAndDeletedAtIsNull(BoardType boardType);

    /**
     * 관리자별 게시판 조회 (삭제되지 않은 것만)
     */
    List<BoardJpaEntity> findByManagerIdAndDeletedAtIsNull(Long managerId);

    /**
     * 삭제되지 않은 게시판 수 조회
     */
    long countByDeletedAtIsNull();

    /**
     * 특정 상태의 삭제되지 않은 게시판 수 조회
     */
    long countByStatusAndDeletedAtIsNull(BoardStatus status);
}