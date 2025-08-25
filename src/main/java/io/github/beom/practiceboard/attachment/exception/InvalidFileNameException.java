package io.github.beom.practiceboard.attachment.exception;

public class InvalidFileNameException extends RuntimeException {
    public InvalidFileNameException(String message) {
        super(message);
    }
    
    public InvalidFileNameException(String message, Throwable cause) {
        super(message, cause);
    }
}