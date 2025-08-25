package io.github.beom.practiceboard.board.application;

import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;

import java.util.List;
import java.util.Optional;

/**
 * Board Repository 인터페이스
 * 게시판 도메인에 대한 데이터 접근을 담당합니다.
 */
public interface BoardRepository {

    /**
     * 게시판 저장
     * @param board 저장할 게시판
     * @return 저장된 게시판 ID
     */
    Long save(Board board);

    /**
     * ID로 게시판 조회
     * @param id 게시판 ID
     * @return 게시판 정보
     */
    Optional<Board> findById(Long id);

    /**
     * 모든 활성 게시판 조회
     * @return 활성 게시판 목록
     */
    List<Board> findAllActive();

    /**
     * 게시판 타입별 조회
     * @param boardType 게시판 타입
     * @return 해당 타입의 게시판 목록
     */
    List<Board> findByType(BoardType boardType);

    /**
     * 게시판 상태별 조회
     * @param status 게시판 상태
     * @return 해당 상태의 게시판 목록
     */
    List<Board> findByStatus(BoardStatus status);

    /**
     * 관리자별 게시판 조회
     * @param managerId 관리자 ID
     * @return 관리자의 게시판 목록
     */
    List<Board> findByManagerId(Long managerId);

    /**
     * 게시판 삭제 (소프트 삭제)
     * @param id 삭제할 게시판 ID
     */
    void deleteById(Long id);

    /**
     * 게시판 존재 여부 확인
     * @param id 게시판 ID
     * @return 존재 여부
     */
    boolean existsById(Long id);

    /**
     * 전체 게시판 수 조회
     * @return 전체 게시판 수
     */
    long count();

    /**
     * 활성 게시판 수 조회
     * @return 활성 게시판 수
     */
    long countActive();
}
