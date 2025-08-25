package io.github.beom.practiceboard.user.mapper;

import io.github.beom.practiceboard.user.domain.UserOAuth;
import io.github.beom.practiceboard.user.infrastructure.UserJpaEntity;
import io.github.beom.practiceboard.user.infrastructure.UserOAuthJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * UserOAuth 도메인 <-> JPA 엔티티 매퍼
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserOAuthMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt") 
    @Mapping(source = "deletedAt", target = "deletedAt")
    UserOAuth toDomain(UserOAuthJpaEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환 (User 정보 필요)
     */
    @Mapping(source = "domain.id", target = "id")
    @Mapping(source = "domain.provider", target = "provider")
    @Mapping(source = "domain.providerId", target = "providerId")
    @Mapping(source = "domain.refreshToken", target = "refreshToken")
    @Mapping(source = "domain.tokenExpiry", target = "tokenExpiry")
    @Mapping(source = "user", target = "user")
    UserOAuthJpaEntity toEntity(UserOAuth domain, UserJpaEntity user);
}