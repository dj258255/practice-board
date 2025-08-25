package io.github.beom.practiceboard.post.exception;

/**
 * 이미 삭제된 게시글에 대한 작업을 시도할 때 발생하는 예외
 */
public class PostAlreadyDeletedException extends RuntimeException {
    
    public PostAlreadyDeletedException(String message) {
        super(message);
    }
    
    public PostAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PostAlreadyDeletedException(Long postId) {
        super("이미 삭제된 게시글입니다: " + postId);
    }
}