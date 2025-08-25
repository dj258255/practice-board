package io.github.beom.practiceboard.board.presentation;

import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardPageRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardResponseDTO;

import java.util.List;

/**
 * 게시판 서비스 인터페이스
 * 게시판(Board) 관련 비즈니스 로직을 처리합니다.
 */
public interface BoardService {

    /**
     * 게시판 생성
     * @param requestDTO 생성할 게시판 정보
     * @return 생성된 게시판 ID
     */
    Long createBoard(BoardRequestDTO requestDTO);

    /**
     * 게시판 조회
     * @param id 조회할 게시판 ID
     * @return 조회된 게시판 정보
     */
    BoardResponseDTO getBoardById(Long id);


    /**
     * 게시판 수정
     * @param id 수정할 게시판 ID
     * @param requestDTO 수정할 게시판 정보
     */
    void updateBoard(Long id, BoardRequestDTO requestDTO);

    /**
     * 게시판 삭제 (소프트 삭제)
     * @param id 삭제할 게시판 ID
     */
    void deleteBoard(Long id);

    /**
     * 게시판 복원
     * @param id 복원할 게시판 ID
     */
    void restoreBoard(Long id);

    /**
     * 모든 활성 게시판 목록 조회
     * @return 활성 게시판 목록
     */
    List<BoardResponseDTO> getAllActiveBoards();

    /**
     * 게시판 타입별 목록 조회
     * @param boardType 게시판 타입
     * @return 해당 타입의 게시판 목록
     */
    List<BoardResponseDTO> getBoardsByType(BoardType boardType);

    /**
     * 페이징된 게시판 목록 조회
     * @param pageRequestDTO 페이징 및 검색 조건
     * @return 페이징된 게시판 목록
     */
    BoardPageResponseDTO<BoardResponseDTO> getBoardList(BoardPageRequestDTO pageRequestDTO);

    /**
     * 관리자별 게시판 목록 조회
     * @param managerId 관리자 ID
     * @return 관리자의 게시판 목록
     */
    List<BoardResponseDTO> getBoardsByManager(Long managerId);

    /**
     * 게시판 활성화
     * @param id 활성화할 게시판 ID
     */
    void activateBoard(Long id);

    /**
     * 게시판 비활성화
     * @param id 비활성화할 게시판 ID
     */
    void deactivateBoard(Long id);

    /**
     * 게시판 관리자 변경
     * @param id 게시판 ID
     * @param newManagerId 새 관리자 ID
     */
    void changeManager(Long id, Long newManagerId);

    /**
     * 게시판 설정 업데이트
     * @param id 게시판 ID
     * @param allowAnonymous 익명 게시 허용 여부
     * @param requireApproval 게시글 승인 필요 여부
     * @param allowAttachment 첨부파일 허용 여부
     * @param maxAttachmentSize 최대 첨부파일 크기
     */
    void updateBoardSettings(Long id, boolean allowAnonymous, boolean requireApproval, 
                           boolean allowAttachment, int maxAttachmentSize);

    /**
     * 게시글 수 증가
     * @param id 게시판 ID
     */
    void incrementPostCount(Long id);

    /**
     * 게시글 수 감소
     * @param id 게시판 ID
     */
    void decrementPostCount(Long id);

    /**
     * 카테고리 수 증가
     * @param id 게시판 ID
     */
    void incrementCategoryCount(Long id);

    /**
     * 카테고리 수 감소
     * @param id 게시판 ID
     */
    void decrementCategoryCount(Long id);


    /**
     * 게시판 통계 조회
     * @param id 게시판 ID
     * @return 게시판 통계 정보
     */
    BoardResponseDTO getBoardStatistics(Long id);

    /**
     * 인기 게시판 목록 조회 (게시글 수 기준)
     * @param limit 결과 개수 제한
     * @return 인기 게시판 목록
     */
    List<BoardResponseDTO> getPopularBoards(int limit);
}