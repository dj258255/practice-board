package io.github.beom.practiceboard.board.exception;

/**
 * 하위 카테고리가 있는 카테고리를 삭제하려고 할 때 발생하는 예외
 */
public class CategoryHasChildrenException extends RuntimeException {
    
    public CategoryHasChildrenException(String message) {
        super(message);
    }
    
    public CategoryHasChildrenException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CategoryHasChildrenException(Long categoryId) {
        super("하위 카테고리가 있는 카테고리는 삭제할 수 없습니다. ID: " + categoryId);
    }
}