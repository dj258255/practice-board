package io.github.beom.practiceboard.global.exception;

import io.github.beom.practiceboard.global.exception.custom.AlreadyDeletedException;
import io.github.beom.practiceboard.global.exception.custom.NotDeletedException;
import io.github.beom.practiceboard.attachment.exception.FileUploadException;
import io.github.beom.practiceboard.attachment.exception.FileNotFoundException;
import io.github.beom.practiceboard.attachment.exception.FileDeleteException;
import io.github.beom.practiceboard.attachment.exception.InvalidFileNameException;
import io.github.beom.practiceboard.board.exception.BoardCategoryNotFoundException;
import io.github.beom.practiceboard.board.exception.BoardNotFoundException;
import io.github.beom.practiceboard.board.exception.CategoryHasChildrenException;
import io.github.beom.practiceboard.board.exception.CircularReferenceException;
import io.github.beom.practiceboard.favorite.exception.FavoriteNotFoundException;
import io.github.beom.practiceboard.favorite.exception.FavoriteAlreadyExistsException;
import io.github.beom.practiceboard.post.exception.*;
import io.github.beom.practiceboard.user.exception.IdExistException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {
    //valid 과정에서 문제가 발생하면 처리할 수 있도록
    //RestControllerAdvice를 설계
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleBindException(BindException e){
        log.error("Validation 오류 발생 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error" , "Validation Failed");
        errorMap.put("message", "입력 데이터 검증에 실패했습니다.");
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        Map<String , String> fieldErrors = new HashMap<>();
        if(e.hasErrors()){
            BindingResult bindingResult = e.getBindingResult();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
        }
        errorMap.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(errorMap);
    }

    //서버의 문제가 아니라 데이터의 문제가 있다고 전송
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String,Object>> handleFKException(Exception e){
        log.error("데이터 무결성 위반 : {}" , e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error" , "Data Integrity Violation");
        errorMap.put("message", "데이터 무결성 위반입니다. 요청을 확인해주세요.");
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }

    //데이터가 존재하지 않는 경우의 처리
    @ExceptionHandler({NoSuchElementException.class,
                EmptyResultDataAccessException.class}) //존재하지 않는 번호의 삭제 예외
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handleNoSuchElement(Exception e){
        log.error("데이터를 찾을 수 없음 : {}" , e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error" , "Not Found");
        errorMap.put("message", "요청한 데이터를 찾을 수 없습니다.");
        errorMap.put("status", 404);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    //이미 삭제된 엔티티 접근
    @ExceptionHandler(AlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String,Object>> handleAlreadyDeleted(AlreadyDeletedException e){
        log.error("이미 삭제된 데이터 접근 : {}" , e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error" , "Already Deleted");
        errorMap.put("message","이미 삭제된 데이터 접근");
        errorMap.put("status" , 409);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }
    //삭제되지 않은 데이터를 복구하려고 할 때
    @ExceptionHandler(NotDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleNotDeleted(NotDeletedException e){
        log.error("삭제되지 않은 데이터 복구 시도 : {} ", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error" , "Not Deleted");
        errorMap.put("message", "삭제되지 않은 데이터 복구 시도");
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

    //파일 업로드 실패
    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String,Object>> handleFileUpload(FileUploadException e){
        log.error("파일 업로드 실패 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "File Upload Failed");
        errorMap.put("message", "파일 업로드에 실패했습니다.");
        errorMap.put("status", 500);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
    }

    //파일을 찾을 수 없음
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handleFileNotFound(FileNotFoundException e){
        log.error("파일을 찾을 수 없음 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "File Not Found");
        errorMap.put("message", "요청한 파일을 찾을 수 없습니다.");
        errorMap.put("status", 404);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    //파일 삭제 실패
    @ExceptionHandler(FileDeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String,Object>> handleFileDelete(FileDeleteException e){
        log.error("파일 삭제 실패 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "File Delete Failed");
        errorMap.put("message", "파일 삭제에 실패했습니다.");
        errorMap.put("status", 500);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
    }

    //잘못된 파일명 형식
    @ExceptionHandler(InvalidFileNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleInvalidFileName(InvalidFileNameException e){
        log.error("잘못된 파일명 형식 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid File Name");
        errorMap.put("message", "올바르지 않은 파일명 형식입니다.");
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

    //게시판 카테고리를 찾을 수 없음
    @ExceptionHandler(BoardCategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handleBoardCategoryNotFound(BoardCategoryNotFoundException e){
        log.error("게시판 카테고리를 찾을 수 없음 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Board Category Not Found");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 404);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    //게시글을 찾을 수 없음
    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handleBoardNotFound(BoardNotFoundException e){
        log.error("게시글을 찾을 수 없음 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Board Not Found");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 404);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    //하위 카테고리가 있는 카테고리 삭제 시도
    @ExceptionHandler(CategoryHasChildrenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleCategoryHasChildren(CategoryHasChildrenException e){
        log.error("하위 카테고리가 있는 카테고리 삭제 시도 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Category Has Children");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

    //순환 참조 발생
    @ExceptionHandler(CircularReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleCircularReference(CircularReferenceException e){
        log.error("순환 참조 발생 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Circular Reference");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

    //좋아요를 찾을 수 없음
    @ExceptionHandler(FavoriteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handleFavoriteNotFound(FavoriteNotFoundException e){
        log.error("좋아요를 찾을 수 없음 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Favorite Not Found");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 404);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    //이미 좋아요가 존재함
    @ExceptionHandler(FavoriteAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String,Object>> handleFavoriteAlreadyExists(FavoriteAlreadyExistsException e){
        log.error("이미 좋아요가 존재함 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Favorite Already Exists");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 409);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }

    //이미 존재하는 사용자 ID
    @ExceptionHandler(IdExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String,Object>> handleIdExist(IdExistException e){
        log.error("이미 존재하는 사용자 ID : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "User ID Already Exists");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 409);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }

    //일반적인 IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException e){
        log.error("잘못된 인수 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid Argument");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

    //일반적인 IllegalStateException 처리
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String,Object>> handleIllegalState(IllegalStateException e){
        log.error("잘못된 상태 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid State");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 409);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMap);
    }

    // === Post 관련 예외 처리 ===
    
    //게시글을 찾을 수 없음
    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handlePostNotFound(PostNotFoundException e){
        log.error("게시글을 찾을 수 없음 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Post Not Found");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 404);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }

    //게시글 접근 권한 없음
    @ExceptionHandler(PostAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Map<String,Object>> handlePostAccessDenied(PostAccessDeniedException e){
        log.error("게시글 접근 권한 없음 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Post Access Denied");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 403);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMap);
    }

    //게시글 데이터 검증 실패
    @ExceptionHandler(PostValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handlePostValidation(PostValidationException e){
        log.error("게시글 데이터 검증 실패 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Post Validation Failed");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

    //이미 삭제된 게시글 접근
    @ExceptionHandler(PostAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ResponseEntity<Map<String,Object>> handlePostAlreadyDeleted(PostAlreadyDeletedException e){
        log.error("이미 삭제된 게시글 접근 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Post Already Deleted");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 410);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.GONE).body(errorMap);
    }

    //게시글과 카테고리 불일치
    @ExceptionHandler(PostCategoryMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handlePostCategoryMismatch(PostCategoryMismatchException e){
        log.error("게시글과 카테고리 불일치 : {}", e.getMessage());

        Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("error", "Post Category Mismatch");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", 400);
        errorMap.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.badRequest().body(errorMap);
    }

}
