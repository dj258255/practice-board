package io.github.beom.practiceboard.favorite.application;

import io.github.beom.practiceboard.favorite.domain.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository {
    
    Favorite save(Favorite favorite);
    
    void delete(Favorite favorite);
    
    Optional<Favorite> findByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
    
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
    
    long countByTargetTypeAndTargetId(String targetType, Long targetId);
    
    List<Favorite> findByUserIdAndTargetType(Long userId, String targetType);
    
    List<Favorite> findByTargetTypeAndTargetId(String targetType, Long targetId);
}