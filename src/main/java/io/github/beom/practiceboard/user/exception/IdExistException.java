package io.github.beom.practiceboard.user.exception;

/**
 * 이미 존재하는 사용자 ID로 회원가입을 시도할 때 발생하는 예외
 */
public class IdExistException extends RuntimeException {
    
    public IdExistException(String message) {
        super(message);
    }
    
    public IdExistException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static IdExistException forUserId(String userId) {
        return new IdExistException("이미 존재하는 사용자 ID입니다: " + userId);
    }
}