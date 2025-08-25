package io.github.beom.practiceboard.favorite.infrastructure;

import io.github.beom.practiceboard.favorite.application.FavoriteRepository;
import io.github.beom.practiceboard.favorite.domain.Favorite;
import io.github.beom.practiceboard.favorite.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 좋아요 레포지토리 구현체
 * 도메인 레포지토리 인터페이스를 JPA로 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepository {
    
    private final FavoriteJpaRepository favoriteJpaRepository;
    private final FavoriteMapper favoriteMapper;
    
    /**
     * 좋아요 저장
     * 
     * @param favorite 저장할 좋아요 도메인 모델
     * @return 저장된 좋아요 도메인 모델
     */
    @Override
    public Favorite save(Favorite favorite) {
        FavoriteJpaEntity entity = favoriteMapper.domainToEntity(favorite);
        FavoriteJpaEntity savedEntity = favoriteJpaRepository.save(entity);
        return favoriteMapper.entityToDomain(savedEntity);
    }
    
    /**
     * 좋아요 삭제
     * 
     * @param favorite 삭제할 좋아요 도메인 모델
     */
    @Override
    public void delete(Favorite favorite) {
        if (favorite.getId() != null) {
            favoriteJpaRepository.deleteById(favorite.getId());
        }
    }
    
    /**
     * 사용자 ID, 대상 타입, 대상 ID로 좋아요 조회
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 도메인 모델 (Optional)
     */
    @Override
    public Optional<Favorite> findByUserIdAndTargetTypeAndTargetId(
            Long userId, String targetType, Long targetId) {
        return favoriteJpaRepository
                .findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .map(favoriteMapper::entityToDomain);
    }
    
    /**
     * 좋아요 존재 여부 확인
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsByUserIdAndTargetTypeAndTargetId(
            Long userId, String targetType, Long targetId) {
        return favoriteJpaRepository
                .existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }
    
    /**
     * 특정 대상의 좋아요 수 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 수
     */
    @Override
    public long countByTargetTypeAndTargetId(String targetType, Long targetId) {
        return favoriteJpaRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }
    
    /**
     * 사용자의 특정 타입 좋아요 목록 조회
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @return 좋아요 도메인 모델 목록
     */
    @Override
    public List<Favorite> findByUserIdAndTargetType(Long userId, String targetType) {
        return favoriteJpaRepository
                .findByUserIdAndTargetType(userId, targetType)
                .stream()
                .map(favoriteMapper::entityToDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 대상의 모든 좋아요 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 도메인 모델 목록
     */
    @Override
    public List<Favorite> findByTargetTypeAndTargetId(String targetType, Long targetId) {
        return favoriteJpaRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId)
                .stream()
                .map(favoriteMapper::entityToDomain)
                .collect(Collectors.toList());
    }
}
