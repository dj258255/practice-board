package io.github.beom.practiceboard.user.presentation;

import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.user.presentation.dto.request.UserRegisterRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 회원가입 API
     * @param userRegisterDTO 회원가입 정보
     * @return 회원가입 결과
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterRequestDTO userRegisterDTO) {
        log.info("사용자 회원가입 요청: {}", userRegisterDTO.getId());

        try {
            userService.join(userRegisterDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("회원가입이 성공적으로 완료되었습니다.");
        } catch (UserService.IdExistException e) {
            log.error("회원가입 실패 - 아이디 중복: {}", userRegisterDTO.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 존재하는 아이디입니다.");
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자 정보 조회 API
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        log.info("사용자 정보 조회 요청: {}", id);

        try {
            User user = userService.findByEmail(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 사용자 정보 수정 API
     * @param id 사용자 ID
     * @param email 새 이메일
     * @return 수정 결과
     */
    @PutMapping("/{id}/email")
    public ResponseEntity<String> updateEmail(@PathVariable String id, @RequestParam String email) {
        log.info("사용자 이메일 수정 요청: {} -> {}", id, email);

        try {
            userService.updateUser(id, email);
            return ResponseEntity.ok("이메일이 성공적으로 수정되었습니다.");
        } catch (RuntimeException e) {
            log.error("이메일 수정 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 사용자 삭제 API (소프트 삭제)
     * @param id 사용자 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        log.info("사용자 삭제 요청: {}", id);

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            log.error("사용자 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 헬스 체크 API
     * @return 상태 메시지
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("사용자 서비스가 정상적으로 동작 중입니다.");
    }
}
