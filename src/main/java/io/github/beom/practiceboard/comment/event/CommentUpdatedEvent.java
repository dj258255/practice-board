package io.github.beom.practiceboard.comment.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 댓글 수정 이벤트
 * 댓글이 수정되었을 때 발생하는 도메인 이벤트입니다.
 */
@Getter
@Builder
@ToString
public class CommentUpdatedEvent implements DomainEvent {

    private final String eventId;
    private final String eventType;
    private final LocalDateTime occurredAt;
    private final String aggregateId;
    private final CommentUpdateData eventData;

    private CommentUpdatedEvent(String eventId, String eventType, LocalDateTime occurredAt, String aggregateId, CommentUpdateData eventData) {
        this.eventId = eventId != null ? eventId : UUID.randomUUID().toString();
        this.eventType = "COMMENT_UPDATED";
        this.occurredAt = occurredAt != null ? occurredAt : LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.eventData = eventData;
    }

    public static CommentUpdatedEvent of(Long commentId, Long boardId, String oldContent, String newContent, String author) {
        CommentUpdateData eventData = CommentUpdateData.builder()
                .commentId(commentId)
                .boardId(boardId)
                .oldContent(oldContent)
                .newContent(newContent)
                .author(author)
                .build();

        return CommentUpdatedEvent.builder()
                .aggregateId(commentId.toString())
                .eventData(eventData)
                .build();
    }

    @Override
    public Object getEventData() {
        return this.eventData;
    }

    @Getter
    @Builder
    @ToString
    public static class CommentUpdateData {
        private final Long commentId;
        private final Long boardId;
        private final String oldContent;
        private final String newContent;
        private final String author;
        private final LocalDateTime updatedAt;

        private CommentUpdateData(Long commentId, Long boardId, String oldContent, String newContent, String author, LocalDateTime updatedAt) {
            this.commentId = commentId;
            this.boardId = boardId;
            this.oldContent = oldContent;
            this.newContent = newContent;
            this.author = author;
            this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        }
    }
}