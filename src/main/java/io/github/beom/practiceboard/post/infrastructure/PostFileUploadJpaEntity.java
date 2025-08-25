package io.github.beom.practiceboard.post.infrastructure;

import io.github.beom.practiceboard.global.base.BaseAllEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시글과 파일 업로드 간의 연관관계를 관리하는 엔티티
 * Post 도메인에서 FileUpload를 참조할 수 있도록 함
 */
@Entity
@Table(name = "post_file_upload")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "postJpaEntity") // 순환 참조 방지
public class PostFileUploadJpaEntity extends BaseAllEntity {

    @Id
    @Column(name = "uuid", nullable = false, length = 50)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostJpaEntity postJpaEntity;

    @Column(nullable = false)
    private String fileName;

    private int ord;

    @Column(nullable = false)
    private boolean img;

    private Long fileSize;

    private String contentType;

    @Column(name = "reference_id")
    private Long referenceId;

    // 엔티티 비교를 위한 equals와 hashCode 메서드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostFileUploadJpaEntity that = (PostFileUploadJpaEntity) o;
        return uuid != null && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}