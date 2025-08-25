package io.github.beom.practiceboard.user.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 도메인 객체
 * 순수한 도메인 로직만 포함합니다.
 */
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roleSet")
public class User {

    private Long id;
    private String email; // 로그인 ID 겸 이메일
    private String password; // 암호화된 비밀번호
    private String name; // 사용자 이름
    private String profileImage; // 프로필 이미지 URL
    private String phone; // 전화번호
    private String bio; // 자기소개
    
    @Builder.Default
    private boolean isDeleted = false; // 소프트 삭제 여부
    
    @Builder.Default
    private boolean isEmailVerified = false; // 이메일 인증 여부
    
    @Builder.Default
    private boolean isActive = true; // 계정 활성화 여부
    
    @Builder.Default
    private Set<UserRole> roleSet = new HashSet<>();
    
    // OAuth 관련 정보
    private UserOAuth oauthInfo;
    
    // 시간 관련 필드
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime lastLoginAt;

    /**
     * 비밀번호 변경
     */
    public User changePassword(String newPassword) {
        return this.toBuilder()
                .password(newPassword)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 이메일 변경
     */
    public User changeEmail(String newEmail) {
        return this.toBuilder()
                .email(newEmail)
                .isEmailVerified(false) // 이메일 변경 시 재인증 필요
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 프로필 정보 업데이트
     */
    public User updateProfile(String name, String phone, String bio, String profileImage) {
        return this.toBuilder()
                .name(name)
                .phone(phone)
                .bio(bio)
                .profileImage(profileImage)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 프로필 이미지만 업데이트
     */
    public User updateProfileImage(String profileImageUrl) {
        return this.toBuilder()
                .profileImage(profileImageUrl)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 사용자 삭제 (소프트 삭제)
     */
    public User delete() {
        return this.toBuilder()
                .isDeleted(true)
                .deletedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 사용자 복구
     */
    public User restore() {
        return this.toBuilder()
                .isDeleted(false)
                .deletedAt(null)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 역할 추가
     */
    public User addRole(UserRole role) {
        Set<UserRole> newRoles = new HashSet<>(this.roleSet);
        newRoles.add(role);
        return this.toBuilder()
                .roleSet(newRoles)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 역할 제거
     */
    public User removeRole(UserRole role) {
        Set<UserRole> newRoles = new HashSet<>(this.roleSet);
        newRoles.remove(role);
        return this.toBuilder()
                .roleSet(newRoles)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 모든 역할 제거
     */
    public User clearRoles() {
        return this.toBuilder()
                .roleSet(new HashSet<>())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 계정 비활성화
     */
    public User deactivate() {
        return this.toBuilder()
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 계정 활성화
     */
    public User activate() {
        return this.toBuilder()
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 이메일 인증 완료
     */
    public User verifyEmail() {
        return this.toBuilder()
                .isEmailVerified(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 마지막 로그인 시간 업데이트
     */
    public User updateLastLogin() {
        return this.toBuilder()
                .lastLoginAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * OAuth 정보 설정
     */
    public User setOAuthInfo(UserOAuth oauthInfo) {
        return this.toBuilder()
                .oauthInfo(oauthInfo)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // === 비즈니스 로직 메서드들 ===
    
    /**
     * 특정 역할을 가지고 있는지 확인
     */
    public boolean hasRole(UserRole role) {
        return roleSet.contains(role);
    }
    
    /**
     * 관리자인지 확인
     */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN) || hasRole(UserRole.SUPER_ADMIN);
    }
    
    /**
     * 일반 사용자인지 확인
     */
    public boolean isUser() {
        return hasRole(UserRole.USER);
    }
    
    /**
     * OAuth 사용자인지 확인
     */
    public boolean isOAuthUser() {
        return oauthInfo != null;
    }
    
    /**
     * 일반 로그인 사용자인지 확인
     */
    public boolean isNormalUser() {
        return password != null && !password.isEmpty();
    }
    
    /**
     * 계정이 사용 가능한 상태인지 확인
     */
    public boolean isUsable() {
        return isActive && !isDeleted && deletedAt == null;
    }
    
    /**
     * 이메일 인증이 필요한지 확인
     */
    public boolean needsEmailVerification() {
        return !isEmailVerified && isNormalUser();
    }
    
    /**
     * 프로필 이미지가 있는지 확인
     */
    public boolean hasProfileImage() {
        return profileImage != null && !profileImage.trim().isEmpty();
    }
}