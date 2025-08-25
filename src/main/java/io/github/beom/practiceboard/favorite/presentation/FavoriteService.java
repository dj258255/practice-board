package io.github.beom.practiceboard.favorite.presentation;

import io.github.beom.practiceboard.favorite.domain.Favorite;

import java.util.List;

/**
 * 좋아요 서비스 인터페이스
 * 좋아요 관련 비즈니스 로직의 계약을 정의합니다.
 */
public interface FavoriteService {
    
    /**
     * 좋아요 추가
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     */
    void addFavorite(Long userId, String targetType, Long targetId);
    
    /**
     * 좋아요 제거
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     */
    void removeFavorite(Long userId, String targetType, Long targetId);
    
    /**
     * 좋아요 토글
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 추가되었으면 true, 취소되었으면 false
     */
    boolean toggleFavorite(Long userId, String targetType, Long targetId);
    
    /**
     * 좋아요 여부 확인
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 여부
     */
    boolean isFavorite(Long userId, String targetType, Long targetId);
    
    /**
     * 좋아요 수 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 수
     */
    long getFavoriteCount(String targetType, Long targetId);
    
    /**
     * 사용자의 좋아요 목록 조회
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @return 좋아요 목록
     */
    List<Favorite> getUserFavorites(Long userId, String targetType);
    
    /**
     * 특정 대상의 좋아요 목록 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 목록
     */
    List<Favorite> getTargetFavorites(String targetType, Long targetId);
}
