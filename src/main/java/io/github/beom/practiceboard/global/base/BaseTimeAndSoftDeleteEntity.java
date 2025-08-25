package io.github.beom.practiceboard.global.base;

import io.github.beom.practiceboard.global.exception.custom.AlreadyDeletedException;
import io.github.beom.practiceboard.global.exception.custom.NotDeletedException;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeAndSoftDeleteEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    //삭제
    public void markDeleted() {
        if (isDeleted()) { throw new AlreadyDeletedException(); }
        this.deletedAt = LocalDateTime.now();
    }

    //확인
    public boolean isDeleted() {
        return deletedAt != null;
    }

    //복구
    public void restore() {
        if (!isDeleted()) { throw new NotDeletedException(); }
        this.deletedAt = null;
    }
}