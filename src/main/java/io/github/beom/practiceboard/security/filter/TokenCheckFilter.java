package io.github.beom.practiceboard.security.filter;

import io.github.beom.practiceboard.security.util.JWTUtil;
import io.github.beom.practiceboard.security.exception.AccessTokenException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(!path.startsWith("/api/")){
            filterChain.doFilter(request,response);
            return;
        }

        log.info("Token Check Filter...........................");
        log.info("JWTUtil: " + jwtUtil);

        //preAuthorize 적용 가능
        //jwt와 PreAuthorize를 이용하는 경우 매번 호출 때마다 UserDetailsService를 이용해서
        //사용자 정보를 다시 로딩해야 하는 단점이 있다.
        //이 과정에서 db호출 피할 수 없어서
        //jwt를 이용하는 의미는 이미 적절한 토큰 소유자가 인증 완료되었다고 가정해야 하므로 가능하다면
        //다시 인증 정보를 구성하는건 성능상 좋은 방식은 아니다.
        try{
            Map<String,Object> payload = validateAccessToken(request);
            // JWT 클레임에서 사용자 정보 추출
            String id = (String)payload.get("id");
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) payload.get("roles");

            log.info("JWT 클레임에서 추출된 사용자 ID: {}", id);
            log.info("JWT 클레임에서 추출된 권한: {}", roles);

            // 클레임 기반으로 Authentication 객체 생성 (DB 호출 없음)
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            id, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request,response);
        } catch (AccessTokenException accessTokenException){
            accessTokenException.sendResponseError(response);
        }
    }

    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization");

        if(headerStr == null || headerStr.length() < 8){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        //Bearer 생략
        String tokenType = headerStr.substring(0,6);
        String tokenStr = headerStr.substring(7);

        if(tokenType.equalsIgnoreCase("Bearer") == false){
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try{
            Map<String,Object> values = jwtUtil.validateToken(tokenStr);

            return values;
        } catch(MalformedJwtException malformedJwtException){
            log.error("MalformedJwtException----------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException){
            log.error("SignatureException---------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch(ExpiredJwtException expiredJwtException){
            log.error("ExpiredJwtException---------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }

    }
}