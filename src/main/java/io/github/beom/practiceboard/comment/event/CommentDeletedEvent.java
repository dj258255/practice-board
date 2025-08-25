package io.github.beom.practiceboard.comment.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 댓글 삭제 이벤트
 * 댓글이 삭제되었을 때 발생하는 도메인 이벤트입니다.
 */
@Getter
@Builder
@ToString
public class CommentDeletedEvent implements DomainEvent {

    private final String eventId;
    private final String eventType;
    private final LocalDateTime occurredAt;
    private final String aggregateId;
    private final CommentDeleteData eventData;

    private CommentDeletedEvent(String eventId, String eventType, LocalDateTime occurredAt, String aggregateId, CommentDeleteData eventData) {
        this.eventId = eventId != null ? eventId : UUID.randomUUID().toString();
        this.eventType = "COMMENT_DELETED";
        this.occurredAt = occurredAt != null ? occurredAt : LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.eventData = eventData;
    }

    public static CommentDeletedEvent of(Long commentId, Long boardId, String content, String author, 
                                       Long parentCommentId, boolean hasChildren) {
        CommentDeleteData eventData = CommentDeleteData.builder()
                .commentId(commentId)
                .boardId(boardId)
                .content(content)
                .author(author)
                .parentCommentId(parentCommentId)
                .hasChildren(hasChildren)
                .build();

        return CommentDeletedEvent.builder()
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
    public static class CommentDeleteData {
        private final Long commentId;
        private final Long boardId;
        private final String content;
        private final String author;
        private final Long parentCommentId;
        private final boolean hasChildren;
        private final LocalDateTime deletedAt;

        private CommentDeleteData(Long commentId, Long boardId, String content, String author, 
                                Long parentCommentId, boolean hasChildren, LocalDateTime deletedAt) {
            this.commentId = commentId;
            this.boardId = boardId;
            this.content = content;
            this.author = author;
            this.parentCommentId = parentCommentId;
            this.hasChildren = hasChildren;
            this.deletedAt = deletedAt != null ? deletedAt : LocalDateTime.now();
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