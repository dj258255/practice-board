package io.github.beom.practiceboard.user.application;

import io.github.beom.practiceboard.user.domain.UserOAuth;

import java.util.Optional;

public interface UserOAuthRepository {
    //이메일과 공급업체로 소셜 사용자 조회
    Optional<UserOAuth> findByEmailAndProvider(String email, String provider);
    //공급업체 ID로 소셜 사용자 조회
    Optional<UserOAuth> findByProviderIdAndProvider(String providerId, String provider);
    //소셜 사용자 저장
    UserOAuth save(UserOAuth userOAuth);
    //이메일로 소셜 사용자 존재 여부 확인
    boolean existsByEmailAndProvider(String email, String provider);
}
