package io.github.beom.practiceboard.board.domain;

/**
 * 게시판 상태
 */
public enum BoardStatus {
    
    /**
     * 활성 상태 - 정상적으로 이용 가능
     */
    ACTIVE("활성"),
    
    /**
     * 비활성 상태 - 일시적으로 이용 불가
     */
    INACTIVE("비활성"),
    
    /**
     * 점검 중 - 관리자가 점검 중
     */
    MAINTENANCE("점검중"),
    
    /**
     * 임시 폐쇄 - 문제 발생으로 임시 폐쇄
     */
    SUSPENDED("임시폐쇄"),
    
    /**
     * 삭제 예정 - 삭제 예정 상태
     */
    PENDING_DELETE("삭제예정");
    
    private final String description;
    
    BoardStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 게시판을 사용할 수 있는 상태인지 확인
     */
    public boolean isUsable() {
        return this == ACTIVE;
    }
    
    /**
     * 읽기만 가능한 상태인지 확인
     */
    public boolean isReadOnly() {
        return this == MAINTENANCE || this == SUSPENDED;
    }
    
    /**
     * 완전히 접근 불가능한 상태인지 확인
     */
    public boolean isBlocked() {
        return this == INACTIVE || this == PENDING_DELETE;
    }
}