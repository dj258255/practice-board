package io.github.beom.practiceboard.board.infrastructure;


import io.github.beom.practiceboard.global.base.BaseAllEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_category")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "children")
public class BoardCategoryJpaEntity extends BaseAllEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    private String description;

    @Column(nullable = false)
    private Long boardId;

    private Long parentId;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private int sortOrder;

    @Builder.Default
    private int recommendThreshold = 10;  // 추천 게시글로 지정되는 기준값 (기본값 10)


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @Builder.Default
    private List<BoardCategoryJpaEntity> children = new ArrayList<>();

}