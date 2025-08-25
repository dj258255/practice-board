package io.github.beom.practiceboard.board.presentation;

import io.github.beom.practiceboard.board.presentation.dto.request.BoardCategoryRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardCategoryResponseDTO;
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
 * 게시판 카테고리 컨트롤러
 * 카테고리 관리 API를 제공
 */
@RestController
@RequestMapping("/api/boards/{boardId}/categories")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "게시판 카테고리 API", description = "게시판 카테고리 관리 API")
public class BoardCategoryController {

    private final BoardCategoryService boardCategoryService;

    /**
     * 카테고리 목록 조회
     * @param boardId 클래스 ID
     * @return 카테고리 목록
     */
    @Operation(summary = "카테고리 목록 조회", description = "클래스별 카테고리 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<BoardCategoryResponseDTO>> getList(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId) {

        log.info("카테고리 목록 조회 -> 클래스 ID: {}", boardId);

        List<BoardCategoryResponseDTO> categoryList = boardCategoryService.getListByBoardId(boardId);

        return ResponseEntity.ok(categoryList);
    }

    /**
     * 루트 카테고리 목록 조회
     * @param boardId 클래스 ID
     * @return 루트 카테고리 목록
     */
    @Operation(summary = "루트 카테고리 목록 조회", description = "클래스별 루트 카테고리 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/roots")
    public ResponseEntity<List<BoardCategoryResponseDTO>> getRootCategories(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId) {

        log.info("루트 카테고리 목록 조회 -> 클래스 ID: {}", boardId);

        List<BoardCategoryResponseDTO> rootCategories = boardCategoryService.getRootCategories(boardId);

        return ResponseEntity.ok(rootCategories);
    }

    /**
     * 하위 카테고리 목록 조회
     * @param boardId 클래스 ID
     * @param parentId 부모 카테고리 ID
     * @return 하위 카테고리 목록
     */
    @Operation(summary = "하위 카테고리 목록 조회", description = "특정 카테고리의 하위 카테고리 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<BoardCategoryResponseDTO>> getSubCategories(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "부모 카테고리 ID", required = true)
            @PathVariable("parentId") Long parentId) {

        log.info("하위 카테고리 목록 조회 -> 클래스 ID: {}, 부모 카테고리 ID: {}", boardId, parentId);

        List<BoardCategoryResponseDTO> subCategories = boardCategoryService.getSubCategories(parentId);

        return ResponseEntity.ok(subCategories);
    }

    /**
     * 카테고리 상세 조회
     * @param boardId 클래스 ID
     * @param id 카테고리 ID
     * @return 카테고리 정보
     */
    @Operation(summary = "카테고리 상세 조회", description = "특정 카테고리의 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoardCategoryResponseDTO> read(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "카테고리 ID", required = true)
            @PathVariable("id") Long id) {

        log.info("카테고리 상세 조회 -> 클래스 ID: {}, 카테고리 ID: {}", boardId, id);

        BoardCategoryResponseDTO categoryDTO = boardCategoryService.readOne(id);

        // 클래스 ID 검증
        if (!boardId.equals(categoryDTO.getBoardId())) {
            log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 실제 카테고리 클래스 ID: {}",
                    boardId, categoryDTO.getBoardId());
            throw new IllegalArgumentException("해당 클래스의 카테고리가 아닙니다.");
        }

        return ResponseEntity.ok(categoryDTO);
    }

    /**
     * 카테고리 등록
     * @param boardId 클래스 ID
     * @param categoryRequestDTO 등록할 카테고리 정보
     * @param bindingResult 유효성 검사 결과
     * @return 등록된 카테고리 ID
     * @throws BindException 유효성 검사 실패 시
     */
    @Operation(summary = "카테고리 등록", description = "새로운 카테고리를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<Map<String, Long>> register(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "등록할 카테고리 정보", required = true)
            @Valid @RequestBody BoardCategoryRequestDTO categoryRequestDTO,
            BindingResult bindingResult) throws BindException {

        log.info("카테고리 등록 -> 클래스 ID: {}, 카테고리: {}", boardId, categoryRequestDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // URL 경로의 클래스 ID 설정
        categoryRequestDTO.setBoardId(boardId);

        Long categoryId = boardCategoryService.register(categoryRequestDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("id", categoryId);

        return ResponseEntity.status(HttpStatus.CREATED).body(resultMap);
    }

    /**
     * 카테고리 수정
     * @param boardId 클래스 ID
     * @param id 수정할 카테고리 ID
     * @param categoryRequestDTO 수정할 카테고리 정보
     * @param bindingResult 유효성 검사 결과
     * @return 수정 결과
     * @throws BindException 유효성 검사 실패 시
     */
    @Operation(summary = "카테고리 수정", description = "기존 카테고리를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> modify(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "수정할 카테고리 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "수정할 카테고리 정보", required = true)
            @Valid @RequestBody BoardCategoryRequestDTO categoryRequestDTO,
            BindingResult bindingResult) throws BindException {

        log.info("카테고리 수정 -> 클래스 ID: {}, 카테고리 ID: {}, 카테고리: {}", boardId, id, categoryRequestDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // 카테고리 ID 설정
        categoryRequestDTO.setId(id);
        categoryRequestDTO.setBoardId(boardId);

        // 기존 카테고리 조회 및 클래스 ID 검증
        BoardCategoryResponseDTO existingCategory = boardCategoryService.readOne(id);
        if (!boardId.equals(existingCategory.getBoardId())) {
            log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 실제 카테고리 클래스 ID: {}",
                    boardId, existingCategory.getBoardId());
            throw new IllegalArgumentException("해당 클래스의 카테고리가 아닙니다.");
        }

        boardCategoryService.modify(categoryRequestDTO);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "success");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 카테고리 삭제
     * @param boardId 클래스 ID
     * @param id 삭제할 카테고리 ID
     * @return 삭제 결과
     */
    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remove(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "삭제할 카테고리 ID", required = true)
            @PathVariable("id") Long id) {

        log.info("카테고리 삭제 -> 클래스 ID: {}, 카테고리 ID: {}", boardId, id);

        // 기존 카테고리 조회 및 클래스 ID 검증
        BoardCategoryResponseDTO existingCategory = boardCategoryService.readOne(id);
        if (!boardId.equals(existingCategory.getBoardId())) {
            log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 실제 카테고리 클래스 ID: {}",
                    boardId, existingCategory.getBoardId());
            throw new IllegalArgumentException("해당 클래스의 카테고리가 아닙니다.");
        }

        boardCategoryService.remove(id);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "success");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 카테고리 이동
     * @param boardId 클래스 ID
     * @param id 이동할 카테고리 ID
     * @param newParentId 새로운 부모 카테고리 ID
     * @return 이동 결과
     */
    @Operation(summary = "카테고리 이동", description = "카테고리의 부모를 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이동 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}/move")
    public ResponseEntity<Map<String, String>> moveCategory(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "이동할 카테고리 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "새로운 부모 카테고리 ID")
            @RequestParam(value = "newParentId", required = false) Long newParentId) {

        log.info("카테고리 이동 -> 클래스 ID: {}, 카테고리 ID: {}, 새 부모 ID: {}", boardId, id, newParentId);

        // 기존 카테고리 조회 및 클래스 ID 검증
        BoardCategoryResponseDTO existingCategory = boardCategoryService.readOne(id);
        if (!boardId.equals(existingCategory.getBoardId())) {
            log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 실제 카테고리 클래스 ID: {}",
                    boardId, existingCategory.getBoardId());
            throw new IllegalArgumentException("해당 클래스의 카테고리가 아닙니다.");
        }

        // 새 부모 카테고리가 있는 경우 클래스 ID 검증
        if (newParentId != null) {
            BoardCategoryResponseDTO newParentCategory = boardCategoryService.readOne(newParentId);
            if (!boardId.equals(newParentCategory.getBoardId())) {
                log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 새 부모 카테고리 클래스 ID: {}",
                        boardId, newParentCategory.getBoardId());
                throw new IllegalArgumentException("새 부모 카테고리가 해당 클래스의 카테고리가 아닙니다.");
            }
        }

        boardCategoryService.moveCategory(id, newParentId);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "success");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 카테고리 활성화/비활성화
     * @param boardId 클래스 ID
     * @param id 카테고리 ID
     * @param isActive 활성화 여부
     * @return 처리 결과
     */
    @Operation(summary = "카테고리 활성화/비활성화", description = "카테고리의 활성화 상태를 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}/active")
    public ResponseEntity<Map<String, String>> setActive(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "카테고리 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "활성화 여부", required = true)
            @RequestParam("isActive") boolean isActive) {

        log.info("카테고리 활성화 상태 변경 -> 클래스 ID: {}, 카테고리 ID: {}, 활성화: {}", boardId, id, isActive);

        // 기존 카테고리 조회 및 클래스 ID 검증
        BoardCategoryResponseDTO existingCategory = boardCategoryService.readOne(id);
        if (!boardId.equals(existingCategory.getBoardId())) {
            log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 실제 카테고리 클래스 ID: {}",
                    boardId, existingCategory.getBoardId());
            throw new IllegalArgumentException("해당 클래스의 카테고리가 아닙니다.");
        }

        boardCategoryService.setActive(id, isActive);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "success");

        return ResponseEntity.ok(resultMap);
    }

    /**
     * 카테고리 추천 게시글 임계값 변경
     * @param boardId 클래스 ID
     * @param id 카테고리 ID
     * @param threshold 추천 임계값
     * @return 처리 결과
     */
    @Operation(summary = "추천 게시글 임계값 변경", description = "카테고리의 추천 게시글 임계값을 변경합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{id}/recommend-threshold")
    public ResponseEntity<Map<String, String>> changeRecommendThreshold(
            @Parameter(description = "클래스 ID", required = true)
            @PathVariable("boardId") Long boardId,
            @Parameter(description = "카테고리 ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "추천 임계값", required = true)
            @RequestParam("threshold") int threshold) {

        log.info("카테고리 추천 임계값 변경 -> 클래스 ID: {}, 카테고리 ID: {}, 임계값: {}", boardId, id, threshold);

        // 기존 카테고리 조회 및 클래스 ID 검증
        BoardCategoryResponseDTO existingCategory = boardCategoryService.readOne(id);
        if (!boardId.equals(existingCategory.getBoardId())) {
            log.warn("권한 없는 접근 시도 - 요청 클래스 ID: {}, 실제 카테고리 클래스 ID: {}",
                    boardId, existingCategory.getBoardId());
            throw new IllegalArgumentException("해당 클래스의 카테고리가 아닙니다.");
        }

        boardCategoryService.changeRecommendThreshold(id, threshold);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", "success");

        return ResponseEntity.ok(resultMap);
    }
}