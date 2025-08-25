package io.github.beom.practiceboard.board.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 게시판 도메인 객체 (새로운 Board 개념)
 * 자유게시판, 공지사항, Q&A 등의 게시판을 나타냅니다.
 */
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Board {
    
    private Long id;
    private String name; // 게시판 이름 (자유게시판, 공지사항, Q&A)
    private String description; // 게시판 설명
    
    @Builder.Default
    private BoardType boardType = BoardType.NORMAL;
    
    @Builder.Default
    private BoardStatus status = BoardStatus.ACTIVE;
    
    // 게시판 설정
    @Builder.Default
    private boolean allowAnonymous = false; // 익명 게시 허용 여부
    
    @Builder.Default
    private boolean requireApproval = false; // 게시글 승인 필요 여부
    
    @Builder.Default
    private boolean allowAttachment = true; // 첨부파일 허용 여부
    
    @Builder.Default
    private int maxAttachmentSize = 10; // 최대 첨부파일 크기 (MB)
    
    // 통계
    @Builder.Default
    private int postCount = 0; // 게시글 수
    
    @Builder.Default
    private int categoryCount = 0; // 카테고리 수
    
    // 관리자 정보
    private Long managerId; // 게시판 관리자 ID
    
    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    /**
     * 게시판이 활성 상태인지 확인
     */
    public boolean isActive() {
        return status == BoardStatus.ACTIVE && deletedAt == null;
    }
    
    /**
     * 게시판이 삭제되었는지 확인
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    /**
     * 익명 게시가 허용되는지 확인
     */
    public boolean isAnonymousAllowed() {
        return allowAnonymous;
    }
    
    /**
     * 첨부파일이 허용되는지 확인
     */
    public boolean isAttachmentAllowed() {
        return allowAttachment;
    }
    
    /**
     * 게시글 승인이 필요한지 확인
     */
    public boolean isApprovalRequired() {
        return requireApproval;
    }
    
    /**
     * 게시글 수 증가
     */
    public Board increasePostCount() {
        return this.toBuilder()
                .postCount(this.postCount + 1)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시글 수 감소
     */
    public Board decreasePostCount() {
        return this.toBuilder()
                .postCount(Math.max(0, this.postCount - 1))
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 카테고리 수 증가
     */
    public Board increaseCategoryCount() {
        return this.toBuilder()
                .categoryCount(this.categoryCount + 1)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 카테고리 수 감소
     */
    public Board decreaseCategoryCount() {
        return this.toBuilder()
                .categoryCount(Math.max(0, this.categoryCount - 1))
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시판 설정 업데이트
     */
    public Board updateSettings(String name, String description, boolean allowAnonymous, 
                                    boolean requireApproval, boolean allowAttachment, int maxAttachmentSize) {
        return this.toBuilder()
                .name(name)
                .description(description)
                .allowAnonymous(allowAnonymous)
                .requireApproval(requireApproval)
                .allowAttachment(allowAttachment)
                .maxAttachmentSize(maxAttachmentSize)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시판 비활성화
     */
    public Board deactivate() {
        return this.toBuilder()
                .status(BoardStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 게시판 활성화
     */
    public Board activate() {
        return this.toBuilder()
                .status(BoardStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 관리자 변경
     */
    public Board changeManager(Long newManagerId) {
        return this.toBuilder()
                .managerId(newManagerId)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}