package io.github.beom.practiceboard.global.event;

import io.github.beom.practiceboard.comment.event.CommentCreatedEvent;
import io.github.beom.practiceboard.comment.event.CommentDeletedEvent;
import io.github.beom.practiceboard.comment.event.CommentUpdatedEvent;
import io.github.beom.practiceboard.global.config.event.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 댓글 이벤트 리스너
 * RabbitMQ를 통해 댓글 이벤트를 수신하고 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class CommentEventListener {

    /**
     * 댓글 생성 이벤트 처리
     * 
     * @param event 댓글 생성 이벤트
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_CREATED_QUEUE)
    public void handleCommentCreated(CommentCreatedEvent event) {
        try {
            log.info("댓글 생성 이벤트 처리 시작: eventId={}, commentId={}, boardId={}", 
                    event.getEventId(), 
                    ((CommentCreatedEvent.CommentEventData) event.getEventData()).getCommentId(), 
                    ((CommentCreatedEvent.CommentEventData) event.getEventData()).getBoardId());

            // 여기서 실제 비즈니스 로직 처리
            // 예: 알림 발송, 게시글 댓글 수 업데이트, 통계 업데이트 등
            
            processCommentCreated(event);
            
            log.info("댓글 생성 이벤트 처리 완료: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("댓글 생성 이벤트 처리 실패: eventId={}, error={}", 
                    event.getEventId(), e.getMessage(), e);
            // 실패한 이벤트 처리 로직 (DLQ, 재시도 등)
        }
    }

    /**
     * 댓글 수정 이벤트 처리
     * 
     * @param event 댓글 수정 이벤트
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_UPDATED_QUEUE)
    public void handleCommentUpdated(CommentUpdatedEvent event) {
        try {
            log.info("댓글 수정 이벤트 처리 시작: eventId={}, commentId={}", 
                    event.getEventId(), 
                    ((CommentUpdatedEvent.CommentUpdateData) event.getEventData()).getCommentId());

            processCommentUpdated(event);
            
            log.info("댓글 수정 이벤트 처리 완료: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("댓글 수정 이벤트 처리 실패: eventId={}, error={}", 
                    event.getEventId(), e.getMessage(), e);
        }
    }

    /**
     * 댓글 삭제 이벤트 처리
     * 
     * @param event 댓글 삭제 이벤트
     */
    @RabbitListener(queues = RabbitMQConfig.COMMENT_DELETED_QUEUE)
    public void handleCommentDeleted(CommentDeletedEvent event) {
        try {
            log.info("댓글 삭제 이벤트 처리 시작: eventId={}, commentId={}", 
                    event.getEventId(), 
                    ((CommentDeletedEvent.CommentDeleteData) event.getEventData()).getCommentId());

            processCommentDeleted(event);
            
            log.info("댓글 삭제 이벤트 처리 완료: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("댓글 삭제 이벤트 처리 실패: eventId={}, error={}", 
                    event.getEventId(), e.getMessage(), e);
        }
    }

    /**
     * 알림용 댓글 이벤트 처리
     * 모든 댓글 이벤트를 알림으로 처리
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleCommentNotification(Object event) {
        try {
            log.info("댓글 알림 이벤트 처리: eventType={}", event.getClass().getSimpleName());
            
            // 알림 처리 로직
            processNotification(event);
            
        } catch (Exception e) {
            log.error("댓글 알림 처리 실패: error={}", e.getMessage(), e);
        }
    }

    /**
     * 댓글 생성 비즈니스 로직 처리
     */
    private void processCommentCreated(CommentCreatedEvent event) {
        CommentCreatedEvent.CommentEventData data = (CommentCreatedEvent.CommentEventData) event.getEventData();
        
        // 1. 게시글 댓글 수 증가
        // 2. 게시글 작성자에게 알림 발송
        // 3. 대댓글인 경우 부모 댓글 작성자에게 알림 발송
        // 4. 통계 업데이트
        
        log.debug("댓글 생성 처리: boardId={}, isReply={}", 
                data.getBoardId(), data.isReply());
    }

    /**
     * 댓글 수정 비즈니스 로직 처리
     */
    private void processCommentUpdated(CommentUpdatedEvent event) {
        // 수정 관련 처리 로직
        log.debug("댓글 수정 처리: commentId={}", 
                ((CommentUpdatedEvent.CommentUpdateData) event.getEventData()).getCommentId());
    }

    /**
     * 댓글 삭제 비즈니스 로직 처리
     */
    private void processCommentDeleted(CommentDeletedEvent event) {
        // 1. 게시글 댓글 수 감소
        // 2. 관련 통계 업데이트
        
        log.debug("댓글 삭제 처리: commentId={}", 
                ((CommentDeletedEvent.CommentDeleteData) event.getEventData()).getCommentId());
    }

    /**
     * 알림 처리
     */
    private void processNotification(Object event) {
        // 실제 알림 발송 로직
        // 예: 이메일, 푸시 알림, 웹소켓 등
        
        log.debug("알림 처리: {}", event);
    }
}