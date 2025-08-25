package io.github.beom.practiceboard.user.application;

import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.user.domain.UserRole;
import io.github.beom.practiceboard.user.presentation.UserService;
import io.github.beom.practiceboard.user.presentation.dto.request.UserRegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void join(UserRegisterRequestDTO requestDTO) throws UserService.IdExistException {
        log.info("사용자 회원가입 시작: {}", requestDTO.getEmail());

        // 이메일 중복 체크
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new UserService.IdExistException("이미 존재하는 이메일입니다: " + requestDTO.getEmail());
        }

        // User 도메인 객체 생성
        User user = User.builder()
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .name(requestDTO.getName())
                .phone(requestDTO.getPhone())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
                .addRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        log.info("사용자 회원가입 완료: {}", savedUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(String id) {
        log.info("사용자 조회: {}", id);
        return userRepository.findByEmail(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.info("사용자 이메일 조회: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    @Override
    public void updateUser(String id, String email) {
        log.info("사용자 이메일 수정: {} -> {}", id, email);

        User user = findById(id);
        User updatedUser = user.changeEmail(email);
        userRepository.save(updatedUser);

        log.info("사용자 이메일 수정 완료: {}", id);
    }

    @Override
    public void deleteUser(String id) {
        log.info("사용자 삭제: {}", id);

        User user = findById(id);
        User deletedUser = user.delete();
        userRepository.save(deletedUser);

        log.info("사용자 삭제 완료: {}", id);
    }
}
