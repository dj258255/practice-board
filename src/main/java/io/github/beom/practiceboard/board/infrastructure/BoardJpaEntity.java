package io.github.beom.practiceboard.board.infrastructure;

import io.github.beom.practiceboard.global.base.BaseAllEntity;
import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시판 JPA 엔티티
 * 실제 게시판(자유게시판, 공지사항 등)을 나타내는 엔티티입니다.
 */
@Entity
@Table(name = "boards")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BoardJpaEntity extends BaseAllEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // 게시판 이름 (자유게시판, 공지사항, Q&A)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 게시판 설명

    @Column(name = "board_type", columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private BoardStatus status;

    // 게시판 설정
    @Column(name = "allow_anonymous", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean allowAnonymous; // 익명 게시 허용 여부

    @Column(name = "require_approval", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean requireApproval; // 게시글 승인 필요 여부

    @Column(name = "allow_attachment", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean allowAttachment; // 첨부파일 허용 여부

    @Column(name = "max_attachment_size", columnDefinition = "INT DEFAULT 10")
    private int maxAttachmentSize; // 최대 첨부파일 크기 (MB)

    // 통계
    @Column(name = "post_count", columnDefinition = "INT DEFAULT 0")
    private int postCount; // 게시글 수

    @Column(name = "category_count", columnDefinition = "INT DEFAULT 0")
    private int categoryCount; // 카테고리 수

    // 관리자 정보
    @Column(name = "manager_id")
    private Long managerId; // 게시판 관리자 ID

    /**
     * Board 도메인 객체로 변환
     */
    public Board toDomain() {
        return Board.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .boardType(this.boardType)
                .status(this.status)
                .allowAnonymous(this.allowAnonymous)
                .requireApproval(this.requireApproval)
                .allowAttachment(this.allowAttachment)
                .maxAttachmentSize(this.maxAttachmentSize)
                .postCount(this.postCount)
                .categoryCount(this.categoryCount)
                .managerId(this.managerId)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .deletedAt(this.getDeletedAt())
                .build();
    }

    /**
     * 게시글 수 증가
     */
    public void incrementPostCount() {
        this.postCount++;
    }

    /**
     * 게시글 수 감소
     */
    public void decrementPostCount() {
        this.postCount = Math.max(0, this.postCount - 1);
    }

    /**
     * 카테고리 수 증가
     */
    public void incrementCategoryCount() {
        this.categoryCount++;
    }

    /**
     * 카테고리 수 감소
     */
    public void decrementCategoryCount() {
        this.categoryCount = Math.max(0, this.categoryCount - 1);
    }
}