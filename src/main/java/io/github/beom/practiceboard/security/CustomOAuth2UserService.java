package io.github.beom.practiceboard.security;

import io.github.beom.practiceboard.user.application.UserRepository;
import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.user.domain.UserRole;
import io.github.beom.practiceboard.security.dto.UserSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("OAuth2 사용자 요청 처리 시작");
        log.info("Client Registration: {}", userRequest.getClientRegistration().getRegistrationId());

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("OAuth2 제공자: {}", clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String,Object> paramMap = oAuth2User.getAttributes();

        String email = null;

        switch(clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
            default:
                log.warn("지원하지 않는 OAuth2 제공자: {}", clientName);
                throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다.");
        }

        log.info("추출된 이메일: {}", email);

        if (email == null || email.isEmpty()) {
            log.error("이메일 정보를 추출할 수 없습니다.");
            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
        }

        // UserSecurityDTO 생성 및 반환
        UserSecurityDTO userSecurityDTO = generateDTO(email, paramMap, clientName);

        log.info("OAuth2 사용자 처리 완료: {} (공급업체: {})", email, clientName);
        return userSecurityDTO;
    }





    /**
     * OAuth2 사용자 정보를 기반으로 UserSecurityDTO 생성
     * 기존 회원은 기존 정보 반환, 신규 회원은 자동 회원가입
     */
    private UserSecurityDTO generateDTO(String email, Map<String,Object> params, String provider){

        Optional<User> result = userRepository.findByEmail(email);

        // 신규 사용자인 경우 자동 회원가입
        if(result.isEmpty()){
            log.info("신규 OAuth2 사용자 자동 회원가입: {}", email);

            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("1111"))
                    .name(email) // OAuth에서 이름을 가져올 수 없는 경우 이메일 사용
                    .build();
            User savedUser = user.addRole(UserRole.USER);
            userRepository.save(savedUser);

            log.info("신규 사용자 회원가입 완료: {}", email);

            // UserSecurityDTO 구성 및 반환
            UserSecurityDTO userSecurityDTO =
                    new UserSecurityDTO(savedUser.getId().toString(), "1111", email, false, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            userSecurityDTO.setProps(params);
            return userSecurityDTO;
        } else {
            // 기존 사용자인 경우 기존 정보 반환
            User user = result.get();
            log.info("기존 OAuth2 사용자 로그인: {}", email);

            UserSecurityDTO userSecurityDTO = new UserSecurityDTO(
                    user.getId().toString(),
                    user.getPassword(),
                    user.getEmail(),
                    user.isDeleted(),
                    user.isOAuthUser(),
                    user.getRoleSet()
                            .stream().map(userRole -> new SimpleGrantedAuthority("ROLE_"+userRole.name()))
                            .collect(Collectors.toList())
            );

            return userSecurityDTO;
        }
    }




    /**
     * 카카오 OAuth2 응답에서 이메일 정보 추출
     */
    private String getKakaoEmail(Map<String,Object> paramMap){
        log.info("카카오 OAuth2 이메일 정보 추출 시작");

        Object value = paramMap.get("kakao_account");

        if (value == null) {
            log.error("카카오 계정 정보가 없습니다.");
            return null;
        }

        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String)accountMap.get("email");

        log.info("카카오에서 추출된 이메일: {}", email);
        return email;
    }
}
