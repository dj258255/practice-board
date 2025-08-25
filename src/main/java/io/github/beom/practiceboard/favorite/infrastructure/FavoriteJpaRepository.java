package io.github.beom.practiceboard.favorite.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 좋아요 JPA 레포지토리
 * 데이터베이스 접근을 위한 JPA 메서드들을 정의합니다.
 */
public interface FavoriteJpaRepository extends JpaRepository<FavoriteJpaEntity, Long> {
    
    /**
     * 사용자 ID, 대상 타입, 대상 ID로 좋아요 조회
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 엔티티 (Optional)
     */
    Optional<FavoriteJpaEntity> findByUserIdAndTargetTypeAndTargetId(
        Long userId, String targetType, Long targetId);
    
    /**
     * 사용자 ID, 대상 타입, 대상 ID로 좋아요 존재 여부 확인
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 존재 여부
     */
    boolean existsByUserIdAndTargetTypeAndTargetId(
        Long userId, String targetType, Long targetId);
    
    /**
     * 특정 대상의 좋아요 수 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 수
     */
    long countByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * 사용자의 특정 타입 좋아요 목록 조회
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @return 좋아요 목록
     */
    List<FavoriteJpaEntity> findByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 특정 대상의 모든 좋아요 조회 (생성일 기준 최신순)
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 목록
     */
    @Query("SELECT f FROM FavoriteJpaEntity f " +
           "WHERE f.targetType = :targetType AND f.targetId = :targetId " +
           "ORDER BY f.createdAt DESC")
    List<FavoriteJpaEntity> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
        @Param("targetType") String targetType, 
        @Param("targetId") Long targetId);
    
    /**
     * 사용자의 모든 좋아요 조회 (생성일 기준 최신순)
     * 
     * @param userId 사용자 ID
     * @return 좋아요 목록
     */
    @Query("SELECT f FROM FavoriteJpaEntity f " +
           "WHERE f.userId = :userId " +
           "ORDER BY f.createdAt DESC")
    List<FavoriteJpaEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * 특정 대상들의 좋아요 수 일괄 조회
     * 
     * @param targetType 대상 타입
     * @param targetIds 대상 ID 목록
     * @return 대상 ID별 좋아요 수 (대상 ID, 좋아요 수)
     */
    @Query("SELECT f.targetId, COUNT(f) FROM FavoriteJpaEntity f " +
           "WHERE f.targetType = :targetType AND f.targetId IN :targetIds " +
           "GROUP BY f.targetId")
    List<Object[]> countByTargetTypeAndTargetIdIn(
        @Param("targetType") String targetType, 
        @Param("targetIds") List<Long> targetIds);
}
