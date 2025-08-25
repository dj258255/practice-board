package io.github.beom.practiceboard.comment.mapper;

import io.github.beom.practiceboard.comment.domain.Comment;
import io.github.beom.practiceboard.comment.infrastructure.CommentJpaEntity;
import io.github.beom.practiceboard.comment.presentation.dto.request.CommentRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.response.CommentResponseDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Comment 매퍼
 * 도메인 모델과 JPA 엔티티, DTO 간의 변환을 담당합니다.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    /**
     * Comment 도메인 객체에서 JpaEntity 생성
     */
    @Mapping(target = "replyText", source = "content") 
    @Mapping(target = "parentComment", expression = "java(createParentCommentReference(comment.getParentReplyId()))")
    CommentJpaEntity fromDomain(Comment comment);
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    @Mapping(target = "content", source = "replyText")
    @Mapping(target = "parentReplyId", expression = "java(entity.getParentComment() != null ? entity.getParentComment().getId() : null)")
    Comment toDomain(CommentJpaEntity entity);

    /**
     * CommentRequestDTO를 Comment 도메인 객체로 변환
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Comment toDomain(CommentRequestDTO requestDTO);

    /**
     * Comment 도메인 객체를 CommentResponseDTO로 변환
     */
    CommentResponseDTO toResponseDTO(Comment comment);

    /**
     * CommentJpaEntity를 CommentResponseDTO로 직접 변환
     */
    @Mapping(target = "content", source = "replyText")
    @Mapping(target = "parentReplyId", expression = "java(entity.getParentComment() != null ? entity.getParentComment().getId() : null)")
    CommentResponseDTO entityToResponseDTO(CommentJpaEntity entity);
    
    /**
     * 부모 댓글 참조 생성
     */
    default CommentJpaEntity createParentCommentReference(Long parentCommentId) {
        if (parentCommentId == null) return null;
        return CommentJpaEntity.builder()
                .id(parentCommentId)
                .build();
    }
    
    /**
     * Comment 도메인 객체의 정보로 기존 JpaEntity 업데이트
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "replyText", source = "content")
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateFromDomain(Comment comment, @MappingTarget CommentJpaEntity entity);
}