package io.github.beom.practiceboard.user.mapper;

import io.github.beom.practiceboard.user.infrastructure.UserProfileImageJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * UserProfileImage JPA 엔티티 매퍼
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileImageMapper {

    /**
     * 파일 정보를 기반으로 UserProfileImageJpaEntity 생성
     */
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "fileUuid", target = "fileUuid")
    @Mapping(source = "originalFileName", target = "originalFileName")
    @Mapping(source = "fileSize", target = "fileSize")
    @Mapping(source = "contentType", target = "contentType")
    @Mapping(source = "s3Url", target = "s3Url")
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl")
    @Mapping(constant = "true", target = "isCurrent")
    UserProfileImageJpaEntity createProfileImage(Long userId, String fileUuid, String originalFileName, 
                                               Long fileSize, String contentType, String s3Url, String thumbnailUrl);
}