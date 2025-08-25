package io.github.beom.practiceboard.security.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {

    @Value("${io.github.beom.jwt.secret}")
    private String key;
    
    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String generateToken(Map<String, Object> valueMap, boolean isAccessToken){
        log.info("generateKey..." + key);

        //헤더
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        //payload부분
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        long expiration = isAccessToken ? accessTokenExpiration : refreshTokenExpiration;

        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());

        String jwtStr = Jwts.builder()
                .header() //헤더 설정
                .add(headers)
                .and()
                .claims(payloads) //페이로드 설정
                .issuedAt(Date.from(ZonedDateTime.now().toInstant())) //발급시간
                .expiration(Date.from(ZonedDateTime.now().plusSeconds(expiration / 1000).toInstant())) //만료시간
                .signWith(secretKey) //서명
                .compact();

        return jwtStr;
    }


    public Map<String, Object> validateToken(String token) throws JwtException {
        Map<String, Object> claim = null;

        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes()); //시크릿 키 생성

        claim = Jwts.parser()
                .verifyWith(secretKey) //검증 키 설정
                .build()
                .parseSignedClaims(token) //서명된 클레임 파심
                .getPayload(); //페이로드 반환

        return claim;
    }
}
