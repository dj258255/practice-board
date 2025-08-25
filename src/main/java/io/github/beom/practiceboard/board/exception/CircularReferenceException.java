package io.github.beom.practiceboard.board.exception;

/**
 * 카테고리 이동 시 순환 참조가 발생할 때 발생하는 예외
 */
public class CircularReferenceException extends RuntimeException {
    
    public CircularReferenceException(String message) {
        super(message);
    }
    
    public CircularReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CircularReferenceException(Long categoryId, Long parentId) {
        super("순환 참조가 발생합니다. 카테고리 ID: " + categoryId + ", 부모 ID: " + parentId);
    }
}