package io.github.beom.practiceboard.board.application;

import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Board(게시판) 검색 Repository 인터페이스
 * 실제 게시판(자유게시판, 공지사항 등) 검색 기능을 위한 인터페이스
 */
public interface BoardSearchRepository {
    
    /**
     * 다중 조건으로 게시판 검색
     * @param types 검색 타입 (n: 게시판명, d: 설명, s: 슬러그)
     * @param keyword 검색 키워드
     * @param boardType 게시판 타입 필터
     * @param status 게시판 상태 필터
     * @param pageable 페이징 정보
     * @return 검색 결과
     */
    Page<BoardResponseDTO> searchBoards(String[] types, 
                                       String keyword,
                                       BoardType boardType,
                                       BoardStatus status,
                                       Pageable pageable);

    /**
     * 활성 게시판만 검색
     * @param types 검색 타입 배열
     * @param keyword 검색 키워드
     * @param boardType 게시판 타입
     * @param pageable 페이징 정보
     * @return 활성 게시판 목록
     */
    Page<BoardResponseDTO> searchActiveBoards(String[] types,
                                             String keyword,
                                             BoardType boardType,
                                             Pageable pageable);

    /**
     * 게시판과 통계 정보를 함께 조회
     * @param types 검색 타입 배열
     * @param keyword 검색 키워드
     * @param boardType 게시판 타입
     * @param pageable 페이징 정보
     * @return 통계 정보가 포함된 게시판 목록
     */
    BoardPageResponseDTO<BoardResponseDTO> searchWithStatistics(String[] types,
                                                               String keyword,
                                                               BoardType boardType,
                                                               Pageable pageable);

    /**
     * 인기 게시판 검색 (게시글 수 기준)
     * @param limit 결과 개수 제한
     * @return 인기 게시판 목록
     */
    Page<BoardResponseDTO> searchPopularBoards(int limit, Pageable pageable);

    /**
     * 관리자별 게시판 검색
     * @param managerId 관리자 ID
     * @param pageable 페이징 정보
     * @return 관리자의 게시판 목록
     */
    Page<BoardResponseDTO> searchBoardsByManager(Long managerId, Pageable pageable);
}
