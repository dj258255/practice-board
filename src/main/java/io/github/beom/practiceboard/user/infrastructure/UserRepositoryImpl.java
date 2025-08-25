package io.github.beom.practiceboard.user.infrastructure;

import io.github.beom.practiceboard.user.application.UserRepository;
import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 리포지토리 구현체
 * Infrastructure 레이어에서 JPA를 통한 데이터 영속성을 담당
 */
@Repository
@RequiredArgsConstructor
@Log4j2
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        log.info("사용자 ID로 조회: {}", id);

        return userJpaRepository.findById(id)
                .map(userMapper::convertToUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("사용자 이메일로 조회: {}", email);

        return userJpaRepository.findByEmail(email)
                .map(userMapper::convertToUser);
    }

    @Override
    public User save(User user) {
        log.info("사용자 저장: {}", user.getId());

        UserJpaEntity jpaEntity = userMapper.convertToJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);

        return userMapper.convertToUser(savedEntity);
    }

    @Override
    public boolean existsById(Long id) {
        log.info("사용자 ID 존재 여부 확인: {}", id);

        return userJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("사용자 삭제: {}", id);

        userJpaRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmailAndSocialProvider(String email, String socialProvider) {
        log.info("사용자 이메일과 소셜 제공자로 조회: {} ({})", email, socialProvider);
        
        // 현재 구현에서는 소셜 제공자별 구분이 없으므로 이메일만으로 조회
        return findByEmail(email);
    }

}
