package io.github.beom.practiceboard.board.exception;

/**
 * 게시판 카테고리를 찾을 수 없을 때 발생하는 예외
 */
public class BoardCategoryNotFoundException extends RuntimeException {
    
    public BoardCategoryNotFoundException(String message) {
        super(message);
    }
    
    public BoardCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BoardCategoryNotFoundException(Long categoryId) {
        super("게시판 카테고리를 찾을 수 없습니다. ID: " + categoryId);
    }
}