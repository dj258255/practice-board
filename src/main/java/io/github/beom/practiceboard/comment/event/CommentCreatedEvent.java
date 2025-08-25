package io.github.beom.practiceboard.comment.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 댓글 생성 이벤트
 * 새로운 댓글이 생성되었을 때 발생하는 도메인 이벤트입니다.
 */
@Getter
@Builder
@ToString
public class CommentCreatedEvent implements DomainEvent {

    private final String eventId;
    private final String eventType;
    private final LocalDateTime occurredAt;
    private final String aggregateId;
    private final CommentEventData eventData;

    /**
     * 댓글 생성 이벤트 생성자
     */
    private CommentCreatedEvent(String eventId, String eventType, LocalDateTime occurredAt, String aggregateId, CommentEventData eventData) {
        this.eventId = eventId != null ? eventId : UUID.randomUUID().toString();
        this.eventType = "COMMENT_CREATED";
        this.occurredAt = occurredAt != null ? occurredAt : LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.eventData = eventData;
    }

    /**
     * 댓글 생성 이벤트 팩토리 메서드
     */
    public static CommentCreatedEvent of(Long commentId, Long boardId, String content, String author, Long parentCommentId, int depth) {
        CommentEventData eventData = CommentEventData.builder()
                .commentId(commentId)
                .boardId(boardId)
                .content(content)
                .author(author)
                .parentCommentId(parentCommentId)
                .depth(depth)
                .build();

        return CommentCreatedEvent.builder()
                .aggregateId(commentId.toString())
                .eventData(eventData)
                .build();
    }

    @Override
    public Object getEventData() {
        return this.eventData;
    }

    /**
     * 댓글 이벤트 데이터
     */
    @Getter
    @Builder
    @ToString
    public static class CommentEventData {
        private final Long commentId;
        private final Long postId;
        private final Long boardId;
        private final String content;
        private final String author;
        private final Long parentCommentId;
        private final int depth;
        private final LocalDateTime createdAt;

        private CommentEventData(Long commentId, Long postId, Long boardId, String content, String author, 
                               Long parentCommentId, int depth, LocalDateTime createdAt) {
            this.commentId = commentId;
            this.postId = postId;
            this.boardId = boardId;
            this.content = content;
            this.author = author;
            this.parentCommentId = parentCommentId;
            this.depth = depth;
            this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        }

        /**
         * 대댓글인지 확인
         */
        public boolean isReply() {
            return parentCommentId != null;
        }

        /**
         * 최상위 댓글인지 확인
         */
        public boolean isRootComment() {
            return parentCommentId == null;
        }
    }
}