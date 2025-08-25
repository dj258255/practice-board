package io.github.beom.practiceboard.global.event;

import io.github.beom.practiceboard.comment.event.DomainEvent;
import io.github.beom.practiceboard.global.config.event.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 이벤트 발행자
 * 도메인 이벤트를 RabbitMQ를 통해 발행합니다.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 댓글 이벤트 발행
     * 
     * @param event 발행할 이벤트
     */
    public void publishCommentEvent(DomainEvent event) {
        try {
            String routingKey = getCommentRoutingKey(event.getEventType());
            
            log.info("댓글 이벤트 발행 시작: eventType={}, eventId={}, aggregateId={}", 
                    event.getEventType(), event.getEventId(), event.getAggregateId());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMENT_EXCHANGE, 
                routingKey, 
                event
            );
            
            log.info("댓글 이벤트 발행 완료: eventType={}, eventId={}", 
                    event.getEventType(), event.getEventId());
            
        } catch (Exception e) {
            log.error("댓글 이벤트 발행 실패: eventType={}, eventId={}, error={}", 
                    event.getEventType(), event.getEventId(), e.getMessage(), e);
            throw new EventPublishException("댓글 이벤트 발행 실패", e);
        }
    }

    /**
     * 일반 이벤트 발행
     * 
     * @param exchange 교환기 이름
     * @param routingKey 라우팅 키
     * @param event 발행할 이벤트
     */
    public void publishEvent(String exchange, String routingKey, Object event) {
        try {
            log.info("이벤트 발행 시작: exchange={}, routingKey={}", exchange, routingKey);
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            
            log.info("이벤트 발행 완료: exchange={}, routingKey={}", exchange, routingKey);
            
        } catch (Exception e) {
            log.error("이벤트 발행 실패: exchange={}, routingKey={}, error={}", 
                    exchange, routingKey, e.getMessage(), e);
            throw new EventPublishException("이벤트 발행 실패", e);
        }
    }

    /**
     * 댓글 이벤트 타입에 따른 라우팅 키 결정
     */
    private String getCommentRoutingKey(String eventType) {
        return switch (eventType) {
            case "COMMENT_CREATED" -> RabbitMQConfig.COMMENT_CREATED_ROUTING_KEY;
            case "COMMENT_UPDATED" -> RabbitMQConfig.COMMENT_UPDATED_ROUTING_KEY;
            case "COMMENT_DELETED" -> RabbitMQConfig.COMMENT_DELETED_ROUTING_KEY;
            default -> throw new IllegalArgumentException("지원하지 않는 댓글 이벤트 타입: " + eventType);
        };
    }

    /**
     * 이벤트 발행 예외
     */
    public static class EventPublishException extends RuntimeException {
        public EventPublishException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}