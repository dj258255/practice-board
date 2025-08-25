package io.github.beom.practiceboard.user.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 OAuth 정보 도메인 모델
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserOAuth {

    private Long id;
    
    private String provider;  // "kakao", "google", "naver" 등
    
    private String providerId;  // 소셜 제공자의 고유 ID
    
    private String refreshToken;  // 소셜 제공자의 리프레시 토큰
    
    private LocalDateTime tokenExpiry;  // 토큰 만료 시간
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime deletedAt;



    // 소셜 제공자별 고유 식별자 생성
    public String getUniqueId() {
        return provider + "_" + providerId;
    }

    // 토큰 만료 확인
    public boolean isTokenExpired() {
        return tokenExpiry != null && LocalDateTime.now().isAfter(tokenExpiry);
    }
    
    // 토큰 정보 업데이트 (불변 객체이므로 새 인스턴스 반환)
    public UserOAuth updateTokens(String refreshToken, LocalDateTime tokenExpiry) {
        return this.toBuilder()
                .refreshToken(refreshToken)
                .tokenExpiry(tokenExpiry)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // 소프트 삭제
    public UserOAuth delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // 복구
    public UserOAuth restore() {
        return this.toBuilder()
                .deletedAt(null)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    // 삭제 여부 확인
    public boolean isDeleted() {
        return deletedAt != null;
    }


}