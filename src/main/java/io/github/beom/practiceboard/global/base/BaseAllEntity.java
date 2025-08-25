package io.github.beom.practiceboard.global.base;

import io.github.beom.practiceboard.global.exception.custom.AlreadyDeletedException;
import io.github.beom.practiceboard.global.exception.custom.NotDeletedException;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


//생성,수정 시간 추적 <- BaseTimeEntity 상속
//생성자/수정자 추적 (직접 구현)
// 소프트 삭제 기능 (직접 구현)
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAllEntity extends BaseTimeEntity {
    
    // Auditable 관련 필드 추가
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    // SoftDelete 관련 필드 추가
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // SoftDelete 관련 메서드들
    public void markDeleted() {
        if (isDeleted()) { 
            throw new AlreadyDeletedException(); 
        }
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void restore() {
        if (!isDeleted()) { 
            throw new NotDeletedException(); 
        }
        this.deletedAt = null;
    }
    
    // 공통 유틸리티 메서드
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}