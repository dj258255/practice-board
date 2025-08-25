package io.github.beom.practiceboard.post.infrastructure;

import io.github.beom.practiceboard.global.base.BaseAllEntity;
import io.github.beom.practiceboard.post.domain.PostType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

/**
 * 게시글 JPA 엔티티
 * 데이터베이스의 posts 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"attachmentSet"})
public class PostJpaEntity extends BaseAllEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "writer", nullable = false, length = 100)
    private String writer;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "post_type", columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
    private long viewCount;

    @Column(name = "like_count", columnDefinition = "BIGINT DEFAULT 0")
    private long likeCount;

    @Column(name = "comment_count", columnDefinition = "BIGINT DEFAULT 0")
    private long commentCount;

    @Column(name = "is_pinned", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isPinned;

    @Column(name = "is_featured", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isFeatured;

    // 첨부파일 관련 연관관계 매핑
    @OneToMany(mappedBy = "postJpaEntity",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<PostFileUploadJpaEntity> attachmentSet = new HashSet<>();
}