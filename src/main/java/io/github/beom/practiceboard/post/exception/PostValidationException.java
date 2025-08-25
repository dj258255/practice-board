package io.github.beom.practiceboard.post.exception;

/**
 * 게시글 데이터 검증 실패 시 발생하는 예외
 */
public class PostValidationException extends RuntimeException {
    
    public PostValidationException(String message) {
        super(message);
    }
    
    public PostValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PostValidationException(String field, String reason) {
        super("게시글 데이터 검증 실패 - 필드: " + field + ", 이유: " + reason);
    }
}