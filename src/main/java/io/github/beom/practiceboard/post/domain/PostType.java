package io.github.beom.practiceboard.post.domain;

/**
 * 게시글 타입
 */
public enum PostType {
    
    /**
     * 일반 게시글
     */
    NORMAL("일반"),
    
    /**
     * 공지사항
     */
    NOTICE("공지"),
    
    /**
     * 긴급 공지
     */
    URGENT("긴급공지"),
    
    /**
     * 고정글 (상단 고정)
     */
    PINNED("고정글"),
    
    /**
     * 이벤트 글
     */
    EVENT("이벤트");
    
    private final String description;
    
    PostType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 공지사항 타입인지 확인
     */
    public boolean isNotice() {
        return this == NOTICE || this == URGENT;
    }
    
    /**
     * 우선순위가 높은 게시글인지 확인
     */
    public boolean isHighPriority() {
        return this == URGENT || this == PINNED;
    }
}