package io.github.beom.practiceboard.board.application;

import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.mapper.BoardMapper;
import io.github.beom.practiceboard.board.presentation.BoardService;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardPageRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 서비스 구현체
 * 게시판 비즈니스 로직을 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardSearchRepository boardSearchRepository;
    private final BoardMapper boardMapper;

    @Override
    public Long createBoard(BoardRequestDTO requestDTO) {
        log.info("게시판 생성: {}", requestDTO.getName());

        // DTO를 도메인 객체로 변환
        Board board = boardMapper.toDomain(requestDTO);
        board = board.toBuilder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        Long boardId = boardRepository.save(board);
        log.info("게시판 생성 완료: {}", boardId);
        return boardId;
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO getBoardById(Long id) {
        log.info("게시판 조회: {}", id);
        
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));
        
        return boardMapper.toResponseDTO(board);
    }


    @Override
    public void updateBoard(Long id, BoardRequestDTO requestDTO) {
        log.info("게시판 수정: {}", id);

        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 게시판이 존재하지 않습니다: " + id));

        Board updatedBoard = existingBoard.updateSettings(
            requestDTO.getName(),
            requestDTO.getDescription(),
            requestDTO.isAllowAnonymous(),
            requestDTO.isRequireApproval(),
            requestDTO.isAllowAttachment(),
            requestDTO.getMaxAttachmentSize()
        ).toBuilder()
                .boardType(requestDTO.getBoardType())
                .status(requestDTO.getStatus())
                .managerId(requestDTO.getManagerId())
                .build();

        boardRepository.save(updatedBoard);
        log.info("게시판 수정 완료: {}", id);
    }

    @Override
    public void deleteBoard(Long id) {
        log.info("게시판 삭제: {}", id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시판이 존재하지 않습니다: " + id));

        // 소프트 삭제
        Board deletedBoard = board.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        boardRepository.save(deletedBoard);
        log.info("게시판 삭제 완료: {}", id);
    }

    @Override
    public void restoreBoard(Long id) {
        log.info("게시판 복원: {}", id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("복원할 게시판이 존재하지 않습니다: " + id));

        Board restoredBoard = board.toBuilder()
                .deletedAt(null)
                .updatedAt(LocalDateTime.now())
                .build();

        boardRepository.save(restoredBoard);
        log.info("게시판 복원 완료: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getAllActiveBoards() {
        log.info("활성 게시판 목록 조회");
        
        return boardRepository.findAllActive().stream()
                .map(boardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getBoardsByType(BoardType boardType) {
        log.info("게시판 타입별 목록 조회: {}", boardType);
        
        return boardRepository.findByType(boardType).stream()
                .map(boardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO<BoardResponseDTO> getBoardList(BoardPageRequestDTO pageRequestDTO) {
        log.info("게시판 목록 조회: {}", pageRequestDTO);

        // 검색 조건이 있으면 검색 리포지토리 사용
        if (pageRequestDTO.hasSearchCondition()) {
            return boardSearchRepository.searchWithStatistics(
                pageRequestDTO.getTypes(),
                pageRequestDTO.getKeyword(),
                BoardType.valueOf(pageRequestDTO.getBoardType() != null ? 
                    pageRequestDTO.getBoardType() : "NORMAL"),
                pageRequestDTO.getPageable("createdAt")
            );
        }

        // 일반 목록 조회
        List<Board> boards = boardRepository.findAllActive();
        List<BoardResponseDTO> dtos = boards.stream()
                .map(boardMapper::toResponseDTO)
                .collect(Collectors.toList());

        return BoardPageResponseDTO.of(dtos, pageRequestDTO.getPage(), 
                pageRequestDTO.getSize(), boards.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getBoardsByManager(Long managerId) {
        log.info("관리자별 게시판 목록 조회: {}", managerId);
        
        return boardRepository.findByManagerId(managerId).stream()
                .map(boardMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void activateBoard(Long id) {
        log.info("게시판 활성화: {}", id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board activatedBoard = board.activate();
        boardRepository.save(activatedBoard);
        log.info("게시판 활성화 완료: {}", id);
    }

    @Override
    public void deactivateBoard(Long id) {
        log.info("게시판 비활성화: {}", id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board deactivatedBoard = board.deactivate();
        boardRepository.save(deactivatedBoard);
        log.info("게시판 비활성화 완료: {}", id);
    }

    @Override
    public void changeManager(Long id, Long newManagerId) {
        log.info("게시판 관리자 변경: {} -> {}", id, newManagerId);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board updatedBoard = board.changeManager(newManagerId);
        boardRepository.save(updatedBoard);
        log.info("게시판 관리자 변경 완료: {}", id);
    }

    @Override
    public void updateBoardSettings(Long id, boolean allowAnonymous, boolean requireApproval, 
                                   boolean allowAttachment, int maxAttachmentSize) {
        log.info("게시판 설정 업데이트: {}", id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board updatedBoard = board.updateSettings(
            board.getName(),
            board.getDescription(),
            allowAnonymous,
            requireApproval,
            allowAttachment,
            maxAttachmentSize
        );

        boardRepository.save(updatedBoard);
        log.info("게시판 설정 업데이트 완료: {}", id);
    }

    @Override
    public void incrementPostCount(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board updatedBoard = board.increasePostCount();
        boardRepository.save(updatedBoard);
    }

    @Override
    public void decrementPostCount(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board updatedBoard = board.decreasePostCount();
        boardRepository.save(updatedBoard);
    }

    @Override
    public void incrementCategoryCount(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board updatedBoard = board.increaseCategoryCount();
        boardRepository.save(updatedBoard);
    }

    @Override
    public void decrementCategoryCount(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));

        Board updatedBoard = board.decreaseCategoryCount();
        boardRepository.save(updatedBoard);
    }


    @Override
    @Transactional(readOnly = true)
    public BoardResponseDTO getBoardStatistics(Long id) {
        log.info("게시판 통계 조회: {}", id);
        
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다: " + id));
        
        return boardMapper.toResponseDTO(board);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getPopularBoards(int limit) {
        log.info("인기 게시판 목록 조회: limit={}", limit);
        
        return boardSearchRepository.searchPopularBoards(limit, null).getContent();
    }
}