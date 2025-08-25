package io.github.beom.practiceboard.security.config;

import com.google.gson.Gson;
import io.github.beom.practiceboard.security.util.JWTUtil;
import io.github.beom.practiceboard.user.application.CustomUserDetailsService;
import io.github.beom.practiceboard.security.handler.Custom403Handler;
import io.github.beom.practiceboard.security.filter.LoginFilter;
import io.github.beom.practiceboard.security.filter.RefreshTokenFilter;
import io.github.beom.practiceboard.security.filter.TokenCheckFilter;
import io.github.beom.practiceboard.security.handler.LoginSuccessHandler;
import io.github.beom.practiceboard.security.handler.CustomSocialLoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j2
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class CustomSecurityConfig {

    //주입 필요
    private final CustomUserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;

    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        log.info("------------------web configure----------------");

        return (web) -> web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations());

    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception{
        log.info("----------------------configure-----------------");
        //AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        //Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        //반드시 필요
        http.authenticationManager(authenticationManager);
        //loginFilter
        LoginFilter loginFilter = new LoginFilter("/generateToken", jwtUtil);
        loginFilter.setAuthenticationManager(authenticationManager);
        //LoginSuccessHandler
        LoginSuccessHandler successHandler = new LoginSuccessHandler(jwtUtil);
        //SuccessHandler 세팅
        loginFilter.setAuthenticationSuccessHandler(successHandler);


        //api로 시작하는 모든 경로는 TokenCheckFfilter 동작
        //LoginFilter의 위치 조정
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(
                tokenCheckFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );
        //refreshToken 호출처리
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken",jwtUtil),
                TokenCheckFilter.class);


        // REST API 보안 설정
        http.csrf(AbstractHttpConfigurer::disable)                                    // CSRF 비활성화 (JWT 토큰 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))// CORS 설정
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .oauth2Login(oauth2 -> oauth2.successHandler(authenticationSuccessHandler()))  // OAuth2 로그인 설정
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())  // 접근 거부 핸들러
                        .authenticationEntryPoint((request, response, authException) -> { // 인증 실패 시 JSON 응답
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");

                            Map<String, Object> responseMap = new HashMap<>();
                            responseMap.put("error", "Unauthorized");
                            responseMap.put("message", "인증이 필요합니다.");
                            responseMap.put("status", 401);

                            Gson gson = new Gson();
                            String jsonResponse = gson.toJson(responseMap);
                            response.getWriter().write(jsonResponse);
                        })
                );

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

    public TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil){
        return new TokenCheckFilter(jwtUtil);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","GET","POST","PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Cache-Control","Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
