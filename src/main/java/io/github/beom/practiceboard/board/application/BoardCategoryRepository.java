package io.github.beom.practiceboard.board.application;

import io.github.beom.practiceboard.board.domain.BoardCategory;

import java.util.List;
import java.util.Optional;

public interface BoardCategoryRepository {
    //카테고리 저장
    Long save(BoardCategory boardCategory);
    //ID로 카테고리 조회
    Optional<BoardCategory> findById(Long id);
    //게시판 ID로 카테고리 목록 조회
    List<BoardCategory> findByBoardId(Long boardId);
    //부모 카테고리 ID로 하위 카테고리 목록 조회
    List<BoardCategory> findByParentId(Long parentId);
    //루트 카테고리 목록 조회(부모가 없는 카테고리)
    List<BoardCategory> findRootCategories(Long boardId);
    //카테고리 삭제
    void deleteById(Long id);
}
