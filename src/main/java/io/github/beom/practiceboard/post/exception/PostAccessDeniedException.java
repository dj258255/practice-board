package io.github.beom.practiceboard.post.exception;

/**
 * 게시글에 대한 접근 권한이 없을 때 발생하는 예외
 */
public class PostAccessDeniedException extends RuntimeException {
    
    public PostAccessDeniedException(String message) {
        super(message);
    }
    
    public PostAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PostAccessDeniedException(Long postId, String reason) {
        super("게시글 접근 권한이 없습니다 - 게시글 ID: " + postId + ", 이유: " + reason);
    }
}