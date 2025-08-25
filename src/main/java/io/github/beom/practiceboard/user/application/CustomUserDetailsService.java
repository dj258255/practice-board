package io.github.beom.practiceboard.user.application;

import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.security.dto.UserSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;



//Spring Security 사용자 인증 서비스
//Application 레이어에서 Spring Security와 도메인 모델을 연결
@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("사용자 인증 시도: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 삭제된 사용자 체크
        if (user.isDeleted()) {
            throw new UsernameNotFoundException("삭제된 사용자입니다: " + username);
        }

        log.info("사용자 인증 성공: {}", username);

        // UserRole enum을 Spring Security GrantedAuthority로 변환
        UserSecurityDTO userSecurityDTO = new UserSecurityDTO(
                user.getEmail(),
                user.getPassword(),
                user.getEmail(),
                user.isDeleted(),
                user.isOAuthUser(),
                user.getRoleSet().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList())
        );

        log.info("사용자 권한: {}", userSecurityDTO.getAuthorities());

        return userSecurityDTO;
    }
}
