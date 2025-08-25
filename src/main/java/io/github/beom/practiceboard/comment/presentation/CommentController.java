package io.github.beom.practiceboard.comment.presentation;

import io.github.beom.practiceboard.comment.presentation.dto.request.CommentPageRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.request.CommentRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.response.CommentResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 댓글 컨트롤러
 * 댓글 관리 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/boards/{boardId}/comments")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "댓글 API", description = "댓글 관리 API")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록
     */
    @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Valid @RequestBody CommentRequestDTO requestDTO,
            BindingResult bindingResult) throws BindException {
        
        log.info("댓글 등록 요청 - 게시글: {}, 댓글: {}", boardId, requestDTO);
        
        // 유효성 검사 오류 처리
        if (bindingResult.hasErrors()) {
            log.warn("댓글 등록 유효성 검사 실패: {}", bindingResult.getAllErrors());
            throw new BindException(bindingResult);
        }
        
        // 게시글 ID 설정
        requestDTO.setBoardId(boardId);
        
        try {
            Long commentId = commentService.register(requestDTO);
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("commentId", commentId);
            resultMap.put("message", "댓글이 성공적으로 등록되었습니다.");
            
            log.info("댓글 등록 성공 - ID: {}", commentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultMap);
            
        } catch (Exception e) {
            log.error("댓글 등록 실패: {}", e.getMessage(), e);
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "댓글 등록에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
    }

    /**
     * 댓글 조회
     */
    @Operation(summary = "댓글 조회", description = "특정 댓글의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> read(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable("commentId") Long commentId) {
        
        log.info("댓글 조회 요청 - 게시글: {}, 댓글: {}", boardId, commentId);
        
        try {
            CommentResponseDTO comment = commentService.read(commentId);
            
            // 게시글 ID 검증
            if (!boardId.equals(comment.getBoardId())) {
                log.warn("잘못된 게시글 접근 - 요청 게시글: {}, 실제 게시글: {}", boardId, comment.getBoardId());
                return ResponseEntity.notFound().build();
            }
            
            log.info("댓글 조회 성공 - ID: {}", commentId);
            return ResponseEntity.ok(comment);
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글을 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("댓글 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 댓글 수정
     */
    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> modify(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequestDTO requestDTO,
            BindingResult bindingResult) throws BindException {
        
        log.info("댓글 수정 요청 - 게시글: {}, 댓글: {}", boardId, commentId);
        
        // 유효성 검사 오류 처리
        if (bindingResult.hasErrors()) {
            log.warn("댓글 수정 유효성 검사 실패: {}", bindingResult.getAllErrors());
            throw new BindException(bindingResult);
        }
        
        // 게시글 ID 설정
        requestDTO.setBoardId(boardId);
        
        try {
            commentService.modify(commentId, requestDTO);
            
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result", "success");
            resultMap.put("message", "댓글이 성공적으로 수정되었습니다.");
            
            log.info("댓글 수정 성공 - ID: {}", commentId);
            return ResponseEntity.ok(resultMap);
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 수정 실패: {}", e.getMessage());
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage(), e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "댓글 수정에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> remove(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "댓글 ID", required = true)
            @PathVariable("commentId") Long commentId) {
        
        log.info("댓글 삭제 요청 - 게시글: {}, 댓글: {}", boardId, commentId);
        
        try {
            commentService.remove(commentId);
            
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result", "success");
            resultMap.put("message", "댓글이 성공적으로 삭제되었습니다.");
            
            log.info("댓글 삭제 성공 - ID: {}", commentId);
            return ResponseEntity.ok(resultMap);
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 삭제 실패: {}", e.getMessage());
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
        } catch (IllegalStateException e) {
            log.warn("댓글 삭제 불가: {}", e.getMessage());
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        } catch (Exception e) {
            log.error("댓글 삭제 실패: {}", e.getMessage(), e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "댓글 삭제에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }

    /**
     * 게시글의 댓글 목록 조회 (페이징)
     */
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 페이징하여 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<BoardPageResponseDTO<CommentResponseDTO>> getList(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @ModelAttribute CommentPageRequestDTO pageRequestDTO) {
        
        log.info("댓글 목록 조회 요청 - 게시글: {}, 페이지: {}", boardId, pageRequestDTO);
        
        // 게시글 ID 설정
        pageRequestDTO.setBoardId(boardId);
        
        try {
            BoardPageResponseDTO<CommentResponseDTO> response = commentService.getListOfBoard(boardId, pageRequestDTO);
            
            log.info("댓글 목록 조회 성공 - 총 {}개", response.getTotal());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("댓글 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 게시글의 계층형 댓글 목록 조회 (페이징)
     */
    @Operation(summary = "계층형 댓글 목록 조회", description = "특정 게시글의 계층형 댓글 목록을 페이징하여 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/hierarchical")
    public ResponseEntity<BoardPageResponseDTO<CommentResponseDTO>> getHierarchicalList(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @ModelAttribute CommentPageRequestDTO pageRequestDTO) {
        
        log.info("계층형 댓글 목록 조회 요청 - 게시글: {}, 페이지: {}", boardId, pageRequestDTO);
        
        // 게시글 ID 설정
        pageRequestDTO.setBoardId(boardId);
        
        try {
            BoardPageResponseDTO<CommentResponseDTO> response = commentService.getHierarchicalListOfBoard(boardId, pageRequestDTO);
            
            log.info("계층형 댓글 목록 조회 성공 - 총 {}개", response.getTotal());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("계층형 댓글 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 댓글의 대댓글 목록 조회
     */
    @Operation(summary = "대댓글 목록 조회", description = "특정 댓글의 대댓글 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CommentResponseDTO>> getChildComments(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "부모 댓글 ID", required = true)
            @PathVariable("parentId") Long parentId) {
        
        log.info("대댓글 목록 조회 요청 - 게시글: {}, 부모 댓글: {}", boardId, parentId);
        
        try {
            List<CommentResponseDTO> childComments = commentService.getChildComments(parentId);
            
            log.info("대댓글 목록 조회 성공 - 총 {}개", childComments.size());
            return ResponseEntity.ok(childComments);
            
        } catch (Exception e) {
            log.error("대댓글 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 댓글 개수 조회
     */
    @Operation(summary = "댓글 개수 조회", description = "특정 게시글의 댓글 개수를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCommentCount(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable("boardId") Long boardId) {
        
        log.info("댓글 개수 조회 요청 - 게시글: {}", boardId);
        
        try {
            long count = commentService.countByBoardId(boardId);
            
            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("count", count);
            
            log.info("댓글 개수 조회 성공 - {}개", count);
            return ResponseEntity.ok(resultMap);
            
        } catch (Exception e) {
            log.error("댓글 개수 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}