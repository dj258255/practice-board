package io.github.beom.practiceboard.board.domain;

/**
 * 게시판 타입
 * 게시판의 용도와 성격을 구분합니다.
 */
public enum BoardType {
    
    /**
     * 일반 게시판 - 자유게시판, 취미 게시판 등
     */
    NORMAL("일반게시판"),
    
    /**
     * 공지사항 게시판 - 중요한 공지사항 전용
     */
    NOTICE("공지사항"),
    
    /**
     * Q&A 게시판 - 질문과 답변 전용
     */
    QNA("질문답변"),
    
    /**
     * 갤러리 게시판 - 이미지 중심 게시판
     */
    GALLERY("갤러리"),
    
    /**
     * 자료실 게시판 - 파일 공유 전용
     */
    ARCHIVE("자료실"),
    
    /**
     * 토론 게시판 - 토론 및 의견 교환
     */
    DISCUSSION("토론"),
    
    /**
     * 이벤트 게시판 - 이벤트 정보 공유
     */
    EVENT("이벤트"),
    
    /**
     * 비공개 게시판 - 특정 권한자만 접근
     */
    PRIVATE("비공개");
    
    private final String description;
    
    BoardType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 공개 게시판인지 확인
     */
    public boolean isPublic() {
        return this != PRIVATE;
    }
    
    /**
     * 파일 업로드가 주목적인 게시판인지 확인
     */
    public boolean isFileOriented() {
        return this == GALLERY || this == ARCHIVE;
    }
    
    /**
     * 관리자 전용 게시판인지 확인
     */
    public boolean isAdminOnly() {
        return this == NOTICE;
    }
    
    /**
     * 문자열을 BoardType으로 안전하게 변환
     */
    public static BoardType safeValueOf(String typeString, BoardType defaultType) {
        if (typeString == null || typeString.trim().isEmpty()) {
            return defaultType;
        }
        try {
            return BoardType.valueOf(typeString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultType;
        }
    }
}