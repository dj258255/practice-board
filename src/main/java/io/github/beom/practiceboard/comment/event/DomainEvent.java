package io.github.beom.practiceboard.comment.event;

import java.time.LocalDateTime;

/**
 * 도메인 이벤트 기본 인터페이스
 * 모든 도메인 이벤트가 구현해야 하는 기본 계약을 정의합니다.
 */
public interface DomainEvent {

    /**
     * 이벤트 ID 조회
     * @return 이벤트 고유 식별자
     */
    String getEventId();

    /**
     * 이벤트 타입 조회
     * @return 이벤트 타입
     */
    String getEventType();

    /**
     * 이벤트 발생 시각 조회
     * @return 이벤트 발생 시각
     */
    LocalDateTime getOccurredAt();

    /**
     * 애그리게이트 ID 조회
     * @return 이벤트를 발생시킨 애그리게이트 ID
     */
    String getAggregateId();

    /**
     * 이벤트 데이터 조회
     * @return 이벤트 관련 데이터
     */
    Object getEventData();
}