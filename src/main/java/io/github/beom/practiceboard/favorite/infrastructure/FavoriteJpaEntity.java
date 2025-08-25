package io.github.beom.practiceboard.favorite.infrastructure;

import io.github.beom.practiceboard.global.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좋아요 JPA 엔티티
 * 데이터베이스와 매핑되는 좋아요 테이블을 표현합니다.
 */
@Entity
@Table(name = "favorites",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_favorite_user_target",
           columnNames = {"user_id", "target_type", "target_id"}
       ),
       indexes = {
           @Index(name = "idx_favorite_target", columnList = "target_type, target_id"),
           @Index(name = "idx_favorite_user", columnList = "user_id"),
           @Index(name = "idx_favorite_user_type", columnList = "user_id, target_type")
       }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteJpaEntity extends BaseTimeEntity {
    
    /**
     * 좋아요 고유 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 좋아요를 누른 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 좋아요 대상 타입 (board, comment 등)
     */
    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;
    
    /**
     * 좋아요 대상 ID
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;
    
    /**
     * JPA 엔티티 생성자
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     */
    public FavoriteJpaEntity(Long userId, String targetType, Long targetId) {
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
    }
    
}
