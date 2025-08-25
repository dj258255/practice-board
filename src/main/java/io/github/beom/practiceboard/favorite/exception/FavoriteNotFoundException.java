package io.github.beom.practiceboard.favorite.exception;

/**
 * 좋아요를 찾을 수 없을 때 발생하는 예외
 */
public class FavoriteNotFoundException extends RuntimeException {
    
    public FavoriteNotFoundException(String message) {
        super(message);
    }
    
    public FavoriteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FavoriteNotFoundException(Long userId, String targetType, Long targetId) {
        super("좋아요를 찾을 수 없습니다. 사용자: " + userId + ", 타입: " + targetType + ", 대상: " + targetId);
    }
}