package io.github.beom.practiceboard.favorite.domain;

import lombok.*;

/**
 * 좋아요 도메인 모델
 * 게시글, 댓글 등 다양한 대상에 대한 사용자 좋아요 정보를 표현합니다.
 */
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Favorite {
    
    /**
     * 좋아요 고유 ID
     */
    private Long id;
    
    /**
     * 좋아요를 누른 사용자 ID
     */
    private Long userId;
    
    /**
     * 좋아요 대상 타입 (board, comment 등)
     */
    private String targetType;
    
    /**
     * 좋아요 대상 ID
     */
    private Long targetId;
    
    /**
     * 좋아요 생성 팩토리 메서드
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 새로운 Favorite 인스턴스
     */
    public static Favorite create(Long userId, String targetType, Long targetId) {
        validateCreateParameters(userId, targetType, targetId);
        
        return Favorite.builder()
                .userId(userId)
                .targetType(targetType)
                .targetId(targetId)
                .build();
    }
    
    /**
     * 특정 타입의 좋아요인지 확인
     * 
     * @param targetType 확인할 타입
     * @return 일치 여부
     */
    public boolean isTargetType(String targetType) {
        return this.targetType != null && this.targetType.equals(targetType);
    }
    
    /**
     * 특정 사용자의 좋아요인지 확인
     * 
     * @param userId 확인할 사용자 ID
     * @return 일치 여부
     */
    public boolean belongsToUser(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }
    
    /**
     * 생성 파라미터 유효성 검증
     */
    private static void validateCreateParameters(Long userId, String targetType, Long targetId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("사용자 ID는 필수이며 양수여야 합니다.");
        }
        if (targetType == null || targetType.trim().isEmpty()) {
            throw new IllegalArgumentException("대상 타입은 필수입니다.");
        }
        if (targetId == null || targetId <= 0) {
            throw new IllegalArgumentException("대상 ID는 필수이며 양수여야 합니다.");
        }
    }
}