package io.github.beom.practiceboard.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30분
public class RedisConfig {
    // Spring Session Redis 설정
    // application.properties의 설정이 자동으로 적용됨
}