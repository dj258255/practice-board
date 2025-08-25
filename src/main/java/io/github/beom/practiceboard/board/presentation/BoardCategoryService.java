package io.github.beom.practiceboard.board.presentation;

import io.github.beom.practiceboard.board.presentation.dto.request.BoardCategoryRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardCategoryResponseDTO;

import java.util.List;

public interface BoardCategoryService {
    /**
     * 카테고리 등록
     */
    Long register(BoardCategoryRequestDTO boardCategoryRequestDTO);
    
    /**
     * 카테고리 조회
     */
    BoardCategoryResponseDTO readOne(Long id);
    
    /**
     * 카테고리 수정
     */
    void modify(BoardCategoryRequestDTO boardCategoryRequestDTO);
    
    /**
     * 카테고리 삭제
     */
    void remove(Long id);
    
    /**
     * 게시판별 카테고리 목록 조회
     */
    List<BoardCategoryResponseDTO> getListByBoardId(Long boardId);
    
    /**
     * 루트 카테고리 목록 조회
     */
    List<BoardCategoryResponseDTO> getRootCategories(Long boardId);
    
    /**
     * 하위 카테고리 목록 조회
     */
    List<BoardCategoryResponseDTO> getSubCategories(Long parentId);
    
    /**
     * 카테고리 이동 -> 부모 카테고리 변경
     */
    void moveCategory(Long id, Long newParentId);
    
    /**
     * 카테고리 활성화/비활성화
     */
    void setActive(Long id, boolean isActive);
    
    /**
     * 카테고리 추천게시글 상승 추천개수 변경
     */
    void changeRecommendThreshold(Long id, int threshold);
}
