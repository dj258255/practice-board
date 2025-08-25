package io.github.beom.practiceboard.board.presentation;

import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardPageRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 게시판 컨트롤러
 * RESTful API 방식으로 게시판 관련 요청을 처리
 */
@RestController
@RequestMapping("/api/v1/boards")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "게시판 API", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시판 목록 조회
     */
    @Operation(summary = "게시판 목록 조회", description = "페이징 및 검색 조건으로 게시판 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BoardPageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<BoardPageResponseDTO<BoardResponseDTO>> list(
            @Parameter(description = "게시판 타입 (NORMAL: 일반, NOTICE: 공지사항)")
            @RequestParam(required = false) String boardType,
            @Parameter(description = "페이지 요청 정보")
            BoardPageRequestDTO pageRequestDTO) {

        log.info("게시판 목록 조회 -> 게시판 타입: {}, 페이지 정보: {}", boardType, pageRequestDTO);

        BoardPageResponseDTO<BoardResponseDTO> responseDTO = boardService.getBoardList(pageRequestDTO);

        log.info("조회 결과 -> 총 {} 건, 현재 페이지: {}", responseDTO.getTotal(), responseDTO.getPage());
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 게시판 생성
     */
    @Operation(summary = "게시판 생성", description = "새로운 게시판을 생성합니다")
    @PostMapping
    public ResponseEntity<Map<String, Long>> createBoard(
            @Parameter(description = "생성할 게시판 정보", required = true)
            @Valid @RequestBody BoardRequestDTO boardRequestDTO,
            BindingResult bindingResult) throws BindException {

        log.info("게시판 생성 요청: {}", boardRequestDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Long boardId = boardService.createBoard(boardRequestDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("id", boardId);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultMap);
    }

    /**
     * 게시판 조회
     */
    @Operation(summary = "게시판 조회", description = "특정 게시판을 ID로 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BoardResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoard(
            @Parameter(description = "조회할 게시판 ID", required = true)
            @PathVariable("id") Long id) {
        log.info("게시판 조회 -> 게시판 ID: {}", id);

        BoardResponseDTO boardResponseDTO = boardService.getBoardById(id);
        return ResponseEntity.ok(boardResponseDTO);
    }


    /**
     * 게시판 수정
     */
    @Operation(summary = "게시판 수정", description = "특정 게시판을 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> updateBoard(
            @Parameter(description = "수정할 게시판 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "수정할 게시판 정보", required = true)
            @Valid @RequestBody BoardRequestDTO boardRequestDTO,
            BindingResult bindingResult) throws BindException {

        log.info("게시판 수정 -> 게시판 ID: {}, 수정 내용: {}", id, boardRequestDTO);

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 오류: {}", bindingResult.getAllErrors());
            throw new BindException(bindingResult);
        }

        boardService.updateBoard(id, boardRequestDTO);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "수정 완료");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 게시판 삭제 (소프트 삭제)
     */
    @Operation(summary = "게시판 삭제", description = "특정 게시판을 논리적으로 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "삭제할 게시판 ID", required = true)
            @PathVariable("id") Long id) {

        log.info("게시판 삭제 -> 게시판 ID: {}", id);
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 삭제된 게시판 복원
     */
    @Operation(summary = "게시판 복원", description = "논리적으로 삭제된 게시판을 복원합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "복원 성공"),
            @ApiResponse(responseCode = "404", description = "게시판을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "이미 복원된 게시판"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restoreBoard(
            @Parameter(description = "복원할 게시판 ID", required = true)
            @PathVariable("id") Long id) {

        log.info("게시판 복원 -> 게시판 ID: {}", id);

        try {
            boardService.restoreBoard(id);

            Map<String, String> result = new HashMap<>();
            result.put("result", "success");
            result.put("message", "게시판이 성공적으로 복원되었습니다.");

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("게시판 복원 실패 - 게시판을 찾을 수 없음: {}", id);
            throw e;
        } catch (IllegalStateException e) {
            log.warn("게시판 복원 실패 - 이미 복원된 게시판: {}", id);
            throw e;
        }
    }

    /**
     * 게시판 활성화
     */
    @Operation(summary = "게시판 활성화", description = "게시판을 활성화합니다")
    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activateBoard(
            @Parameter(description = "활성화할 게시판 ID", required = true)
            @PathVariable("id") Long id) {

        log.info("게시판 활성화 -> 게시판 ID: {}", id);
        boardService.activateBoard(id);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "활성화 완료");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 게시판 비활성화
     */
    @Operation(summary = "게시판 비활성화", description = "게시판을 비활성화합니다")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateBoard(
            @Parameter(description = "비활성화할 게시판 ID", required = true)
            @PathVariable("id") Long id) {

        log.info("게시판 비활성화 -> 게시판 ID: {}", id);
        boardService.deactivateBoard(id);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "비활성화 완료");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 게시판 타입별 목록 조회
     */
    @Operation(summary = "타입별 게시판 목록", description = "특정 타입의 게시판 목록을 조회합니다")
    @GetMapping("/type/{boardType}")
    public ResponseEntity<List<BoardResponseDTO>> getBoardsByType(
            @Parameter(description = "게시판 타입", required = true)
            @PathVariable("boardType") BoardType boardType) {

        log.info("타입별 게시판 목록 조회 -> 타입: {}", boardType);
        List<BoardResponseDTO> boards = boardService.getBoardsByType(boardType);
        return ResponseEntity.ok(boards);
    }

    /**
     * 인기 게시판 목록 조회
     */
    @Operation(summary = "인기 게시판 목록", description = "게시글 수 기준 인기 게시판 목록을 조회합니다")
    @GetMapping("/popular")
    public ResponseEntity<List<BoardResponseDTO>> getPopularBoards(
            @Parameter(description = "결과 개수 제한", required = false)
            @RequestParam(defaultValue = "10") int limit) {

        log.info("인기 게시판 목록 조회 -> limit: {}", limit);
        List<BoardResponseDTO> boards = boardService.getPopularBoards(limit);
        return ResponseEntity.ok(boards);
    }

    /**
     * 게시판 관리자 변경
     */
    @Operation(summary = "게시판 관리자 변경", description = "게시판의 관리자를 변경합니다")
    @PatchMapping("/{id}/manager")
    public ResponseEntity<Map<String, String>> changeManager(
            @Parameter(description = "게시판 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "새 관리자 ID", required = true)
            @RequestParam Long newManagerId) {

        log.info("게시판 관리자 변경 -> 게시판 ID: {}, 새 관리자 ID: {}", id, newManagerId);
        boardService.changeManager(id, newManagerId);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "관리자 변경 완료");

        return ResponseEntity.ok(resultMap);
    }
}
