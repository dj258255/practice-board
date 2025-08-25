package io.github.beom.practiceboard.post.exception;

/**
 * 게시글과 카테고리가 일치하지 않을 때 발생하는 예외
 */
public class PostCategoryMismatchException extends RuntimeException {
    
    public PostCategoryMismatchException(String message) {
        super(message);
    }
    
    public PostCategoryMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PostCategoryMismatchException(Long postId, Long categoryId) {
        super("게시글과 카테고리가 일치하지 않습니다 - 게시글 ID: " + postId + ", 카테고리 ID: " + categoryId);
    }
}