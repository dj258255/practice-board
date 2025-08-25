package io.github.beom.practiceboard.security.handler;

import com.google.gson.Gson;
import io.github.beom.practiceboard.security.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("로그인 성공 핸들러..................................");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info(authentication);
        log.info(authentication.getName()); //유저이름

        // JWT 클레임에 사용자 정보와 권한 포함
        Map<String,Object> claim = Map.of(
                "id", authentication.getName(),
                "roles", authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList()
        );
        //액세스 토큰 생성
        String accessToken = jwtUtil.generateToken(claim, true);
        //Refresh Token 생성
        String refreshToken = jwtUtil.generateToken(claim, false);

        Gson gson = new Gson();
        Map<String,String> keyMap = Map.of(
                "accessToken",accessToken,
                "refreshToken",refreshToken);

        String jsonStr = gson.toJson(keyMap);

        response.getWriter().println(jsonStr);

    }
}
