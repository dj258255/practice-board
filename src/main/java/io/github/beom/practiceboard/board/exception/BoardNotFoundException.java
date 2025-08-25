package io.github.beom.practiceboard.board.exception;

/**
 * 게시글을 찾을 수 없을 때 발생하는 예외
 */
public class BoardNotFoundException extends RuntimeException {
    
    public BoardNotFoundException(String message) {
        super(message);
    }
    
    public BoardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BoardNotFoundException(Long boardId) {
        super("게시글을 찾을 수 없습니다. ID: " + boardId);
    }
}