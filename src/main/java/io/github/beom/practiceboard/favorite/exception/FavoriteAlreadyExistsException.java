package io.github.beom.practiceboard.favorite.exception;

/**
 * 이미 좋아요가 존재할 때 발생하는 예외
 */
public class FavoriteAlreadyExistsException extends RuntimeException {
    
    public FavoriteAlreadyExistsException(String message) {
        super(message);
    }
    
    public FavoriteAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FavoriteAlreadyExistsException(Long userId, String targetType, Long targetId) {
        super("이미 좋아요가 존재합니다. 사용자: " + userId + ", 타입: " + targetType + ", 대상: " + targetId);
    }
}