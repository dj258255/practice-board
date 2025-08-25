package io.github.beom.practiceboard.favorite.domain;

import java.util.Set;

/**
 * 좋아요 대상 타입 상수 클래스
 * 좋아요가 가능한 모든 대상 타입을 중앙에서 관리합니다.
 */
public final class FavoriteTargetType {
    
    /**
     * 게시글 타입
     */
    public static final String BOARD = "board";
    
    /**
     * 댓글 타입
     */
    public static final String COMMENT = "comment";
    
    /**
     * 향후 확장 가능한 타입들
     * 새로운 좋아요 대상이 생길 때 이곳에 추가하고 VALID_TYPES에도 포함시켜야 합니다.
     */
    // public static final String REPLY = "reply";        // 답글
    // public static final String ARTICLE = "article";    // 기사
    // public static final String PHOTO = "photo";        // 사진
    // public static final String VIDEO = "video";        // 비디오
    
    /**
     * 유효한 타입들의 집합
     * 새로운 타입 추가 시 이곳에도 반드시 추가해야 합니다.
     */
    private static final Set<String> VALID_TYPES = Set.of(BOARD, COMMENT);
    
    /**
     * 인스턴스 생성 방지
     */
    private FavoriteTargetType() {
        throw new AssertionError("이 클래스는 인스턴스화할 수 없습니다.");
    }
    
    /**
     * 주어진 타입이 유효한 좋아요 대상 타입인지 검증합니다.
     * 
     * @param targetType 검증할 타입
     * @return 유효한 타입이면 true, 그렇지 않으면 false
     */
    public static boolean isValid(String targetType) {
        return targetType != null && VALID_TYPES.contains(targetType);
    }
    
    /**
     * 모든 유효한 타입을 반환합니다.
     * 
     * @return 유효한 타입들의 불변 집합
     */
    public static Set<String> getAllTypes() {
        return VALID_TYPES;
    }
    
    /**
     * 타입의 개수를 반환합니다.
     * 
     * @return 총 타입 개수
     */
    public static int getTypeCount() {
        return VALID_TYPES.size();
    }
}