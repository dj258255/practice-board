package io.github.beom.practiceboard.user.infrastructure;

import io.github.beom.practiceboard.global.base.BaseTimeAndSoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 OAuth 정보 JPA 엔티티
 * 사용자의 소셜 로그인 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "user_oauth", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "user")
public class UserOAuthJpaEntity extends BaseTimeAndSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(nullable = false, length = 50)
    private String provider;  // "kakao", "google", "naver" 등

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;  // 소셜 제공자의 고유 ID

    @Column(length = 1000)
    private String refreshToken;  // 소셜 제공자의 리프레시 토큰

    @Column
    private LocalDateTime tokenExpiry;  // 토큰 만료 시간


    // 토큰 정보 업데이트
    public void updateTokens(String refreshToken, LocalDateTime tokenExpiry) {
        this.refreshToken = refreshToken;
        this.tokenExpiry = tokenExpiry;
    }

    // OAuth 정보 업데이트
    public void updateOAuthInfo(String providerId, String refreshToken, LocalDateTime tokenExpiry) {
        this.providerId = providerId;
        this.refreshToken = refreshToken;
        this.tokenExpiry = tokenExpiry;
    }
}