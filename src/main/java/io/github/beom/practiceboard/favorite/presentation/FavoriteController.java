package io.github.beom.practiceboard.favorite.presentation;

import io.github.beom.practiceboard.favorite.domain.Favorite;
import io.github.beom.practiceboard.favorite.domain.FavoriteTargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 좋아요 컨트롤러
 * 좋아요 관련 REST API 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    /**
     * 좋아요 추가
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 성공 응답
     */
    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        
        favoriteService.addFavorite(userId, targetType, targetId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 좋아요 제거
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 성공 응답
     */
    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        
        favoriteService.removeFavorite(userId, targetType, targetId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 좋아요 토글
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 상태 (true: 추가됨, false: 제거됨)
     */
    @PostMapping("/toggle")
    public ResponseEntity<Boolean> toggleFavorite(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        
        boolean isAdded = favoriteService.toggleFavorite(userId, targetType, targetId);
        return ResponseEntity.ok(isAdded);
    }
    
    /**
     * 좋아요 여부 확인
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 여부
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isFavorite(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        
        boolean isFavorite = favoriteService.isFavorite(userId, targetType, targetId);
        return ResponseEntity.ok(isFavorite);
    }
    
    /**
     * 좋아요 수 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 수
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getFavoriteCount(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        
        long count = favoriteService.getFavoriteCount(targetType, targetId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * 사용자의 좋아요 목록 조회
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입 (선택사항)
     * @return 좋아요 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(required = false) String targetType) {
        
        List<Favorite> favorites;
        if (targetType != null) {
            favorites = favoriteService.getUserFavorites(userId, targetType);
        } else {
            // 모든 타입의 좋아요 조회
            favorites = favoriteService.getUserFavorites(userId, FavoriteTargetType.BOARD);
            favorites.addAll(favoriteService.getUserFavorites(userId, FavoriteTargetType.COMMENT));
        }
        
        return ResponseEntity.ok(favorites);
    }
    
    /**
     * 특정 대상의 좋아요 목록 조회
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 목록
     */
    @GetMapping("/target")
    public ResponseEntity<List<Favorite>> getTargetFavorites(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        
        List<Favorite> favorites = favoriteService.getTargetFavorites(targetType, targetId);
        return ResponseEntity.ok(favorites);
    }
}
