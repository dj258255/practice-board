package io.github.beom.practiceboard.post.mapper;

import io.github.beom.practiceboard.attachment.domain.Attachment;
import io.github.beom.practiceboard.global.config.MapStructConfig;
import io.github.beom.practiceboard.post.domain.Post;
import io.github.beom.practiceboard.post.infrastructure.PostJpaEntity;
import io.github.beom.practiceboard.post.infrastructure.PostFileUploadJpaEntity;
import io.github.beom.practiceboard.post.presentation.dto.request.PostRequestDTO;
import io.github.beom.practiceboard.post.presentation.dto.response.PostResponseDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;

/**
 * Post 도메인과 PostJpaEntity, DTO 간의 매핑을 담당하는 MapStruct 인터페이스
 */
@Mapper(config = MapStructConfig.class)
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    /**
     * Post 도메인 객체를 PostJpaEntity로 변환
     */
    PostJpaEntity toEntity(Post post);

    /**
     * PostJpaEntity를 Post 도메인 객체로 변환
     */
    @Mapping(target = "attachments", source = "attachmentSet", qualifiedByName = "mapAttachments")
    Post toDomain(PostJpaEntity entity);

    /**
     * PostRequestDTO를 Post 도메인 객체로 변환
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Post toDomain(PostRequestDTO requestDTO);

    /**
     * Post 도메인 객체를 PostResponseDTO로 변환
     */
    @Mapping(target = "boardName", ignore = true) // 별도 처리 필요
    @Mapping(target = "categoryName", ignore = true) // 별도 처리 필요
    @Mapping(target = "authorProfileImage", ignore = true) // 별도 처리 필요
    @Mapping(target = "authorEmail", ignore = true) // 별도 처리 필요
    @Mapping(target = "isLikedByCurrentUser", ignore = true) // 별도 처리 필요
    @Mapping(target = "isFavoritedByCurrentUser", ignore = true) // 별도 처리 필요
    @Mapping(target = "canEdit", ignore = true) // 별도 처리 필요
    @Mapping(target = "canDelete", ignore = true) // 별도 처리 필요
    @Mapping(target = "contentSummary", ignore = true) // getter에서 처리
    PostResponseDTO toResponseDTO(Post post);

    /**
     * PostFileUploadJpaEntity Set을 Attachment Set으로 변환
     */
    @Named("mapAttachments")
    default Set<Attachment> mapAttachments(Set<PostFileUploadJpaEntity> attachmentSet) {
        if (attachmentSet == null || attachmentSet.isEmpty()) {
            return null;
        }
        
        return attachmentSet.stream()
                .map(this::toAttachment)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * PostFileUploadJpaEntity를 Attachment로 변환
     */
    @Mapping(target = "domain", constant = "post")
    @Mapping(target = "updatedAt", source = "createdAt") // 수정: uploadAt -> updatedAt
    @Mapping(target = "createdBy", source = "createdBy") // 수정: uploadedBy -> createdBy (BaseAllEntity에서 가져옴)
    Attachment toAttachment(PostFileUploadJpaEntity fileEntity);
}