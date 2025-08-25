package io.github.beom.practiceboard.board.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시판 카테고리 도메인 객체
 * 게시판 내의 카테고리를 나타냅니다. (일상, 취미, 기술, 질문 등)
 */
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class BoardCategory {
    
    private Long id;
    private String categoryName;
    private String description;
    
    // 게시판 참조 (새로운 구조)
    private Long boardId; // 어떤 게시판에 속하는 카테고리인지
    
    private Long createdBy;
    private Long updatedBy;
    private Long parentId; // 부모 카테고리 (계층 구조)
    
    @Builder.Default
    private boolean isActive = true;
    
    @Builder.Default
    private int sortOrder = 0; // 정렬 순서
    
    @Builder.Default
    private int postCount = 0; // 이 카테고리의 게시글 수
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // 추천 게시글로 저장되는 기준 값 (기본값 10)
    @Builder.Default
    private int recommendThreshold = 10;
    
    @Builder.Default
    private List<BoardCategory> children = new ArrayList<>();

    /**
     * 카테고리명 변경
     */
    public BoardCategory changeName(String categoryName, Long updatedBy) {
        return this.toBuilder()
                .categoryName(categoryName)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 카테고리를 다른 부모로 이동
     */
    public BoardCategory moveTo(Long newParentId, Long updatedBy) {
        return this.toBuilder()
                .parentId(newParentId)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 카테고리를 다른 게시판으로 이동
     */
    public BoardCategory moveToBoardAndParent(Long newBoardId, Long newParentId, Long updatedBy) {
        return this.toBuilder()
                .boardId(newBoardId)
                .parentId(newParentId)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 하위 카테고리들 설정
     */
    public BoardCategory withChildren(List<BoardCategory> children) {
        return this.toBuilder()
                .children(children)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 루트 카테고리인지 확인
     */
    public boolean isRootCategory() {
        return parentId == null;
    }
    
    /**
     * 하위 카테고리가 있는지 확인
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    /**
     * 특정 깊이 확인 (최대 3단계)
     */
    public int getDepth() {
        if (parentId == null) return 1;
        return 2; // 현재는 2단계만 지원
    }
    
    /**
     * 카테고리 활성화
     */
    public BoardCategory activate(Long updatedBy) {
        return this.toBuilder()
                .isActive(true)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 카테고리 비활성화
     */
    public BoardCategory deactivate(Long updatedBy) {
        return this.toBuilder()
                .isActive(false)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 추천 게시글 기준값 변경
     */
    public BoardCategory changeRecommendThreshold(int newThreshold, Long updatedBy) {
        if (newThreshold < 0) {
            throw new IllegalArgumentException("추천 게시글 기준값은 0 이상이어야 합니다.");
        }

        return this.toBuilder()
                .recommendThreshold(newThreshold)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시글 수 증가
     */
    public BoardCategory increasePostCount() {
        return this.toBuilder()
                .postCount(this.postCount + 1)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시글 수 감소
     */
    public BoardCategory decreasePostCount() {
        return this.toBuilder()
                .postCount(Math.max(0, this.postCount - 1))
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 정렬 순서 변경
     */
    public BoardCategory changeSortOrder(int newSortOrder, Long updatedBy) {
        return this.toBuilder()
                .sortOrder(newSortOrder)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 카테고리 설명 업데이트
     */
    public BoardCategory updateDescription(String description, Long updatedBy) {
        return this.toBuilder()
                .description(description)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    
    /**
     * 카테고리가 삭제되었는지 확인
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    /**
     * 같은 게시판에 속하는지 확인
     */
    public boolean belongsToBoard(Long boardId) {
        return this.boardId != null && this.boardId.equals(boardId);
    }
}