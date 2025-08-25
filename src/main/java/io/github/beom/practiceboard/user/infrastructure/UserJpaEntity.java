package io.github.beom.practiceboard.user.infrastructure;

import io.github.beom.practiceboard.global.base.BaseTimeAndSoftDeleteEntity;
import io.github.beom.practiceboard.user.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 JPA 엔티티
 * 데이터베이스 테이블과 매핑되는 인프라스트럭처 레이어의 엔티티입니다.
 */
@Entity
@Table(name = "users")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class UserJpaEntity extends BaseTimeAndSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private String name;

    private String profileImage;

    private String phone;

    @Lob
    private String bio;

    @Builder.Default
    @Column(nullable = false)
    private boolean isEmailVerified = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<UserRole> roleSet = new HashSet<>();

    private LocalDateTime lastLoginAt;

    // OAuth 관련 정보 (별도 엔티티로 분리 가능)
    private String oauthProvider;
    private String oauthId;
}
