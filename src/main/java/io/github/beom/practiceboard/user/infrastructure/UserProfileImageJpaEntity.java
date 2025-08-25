package io.github.beom.practiceboard.user.infrastructure;

import io.github.beom.practiceboard.global.base.BaseTimeAndSoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 프로필 이미지 JPA 엔티티
 */
@Entity
@Table(name = "user_profile_images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class UserProfileImageJpaEntity extends BaseTimeAndSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_uuid", nullable = false, length = 50)
    private String fileUuid;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "s3_url", nullable = false, length = 500)
    private String s3Url;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private Boolean isCurrent = true;

    // 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;

    // 현재 프로필 이미지로 설정
    public void setAsCurrent() {
        this.isCurrent = true;
    }

    // 현재 프로필 이미지에서 해제
    public void unsetAsCurrent() {
        this.isCurrent = false;
    }

    // 프로필 이미지 정보 업데이트
    public void updateImageInfo(String s3Url, String thumbnailUrl) {
        this.s3Url = s3Url;
        this.thumbnailUrl = thumbnailUrl;
    }
}