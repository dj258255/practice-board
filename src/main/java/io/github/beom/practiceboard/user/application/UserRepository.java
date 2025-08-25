package io.github.beom.practiceboard.user.application;

import io.github.beom.practiceboard.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsById(Long id);
    void deleteById(Long id);
    Optional<User> findByEmailAndSocialProvider(String email, String socialProvider);
}
