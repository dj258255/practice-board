package io.github.beom.practiceboard.security.handler;

import com.google.gson.Gson;
import io.github.beom.practiceboard.security.dto.UserSecurityDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("--------------------------------------------------------------------");
        log.info("소셜 로그인 성공 핸들러 onAuthenticationSuccess .........................");
        log.info(authentication.getPrincipal());

        UserSecurityDTO userSecurityDTO = (UserSecurityDTO) authentication.getPrincipal();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        // 소셜 로그인 성공 시 사용자 정보를 JSON으로 반환
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", userSecurityDTO.getEmail());
        userMap.put("name", userSecurityDTO.getName());
        userMap.put("social", userSecurityDTO.isSocial());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("message", "소셜 로그인 성공");
        responseMap.put("user", userMap);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(responseMap);
        response.getWriter().write(jsonResponse);
    }
}
