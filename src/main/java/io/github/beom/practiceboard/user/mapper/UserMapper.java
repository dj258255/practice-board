package io.github.beom.practiceboard.user.mapper;

import io.github.beom.practiceboard.user.domain.User;
import io.github.beom.practiceboard.user.domain.UserOAuth;
import io.github.beom.practiceboard.user.infrastructure.UserJpaEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * User 도메인과 UserJpaEntity 간의 매핑을 담당하는 MapStruct 인터페이스
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * User 도메인 객체를 UserJpaEntity로 변환
     */
    UserJpaEntity toEntity(User user);

    /**
     * UserJpaEntity를 User 도메인 객체로 변환
     */
    @Mapping(target = "oauthInfo", expression = "java(mapOAuthInfo(entity))")
    User toDomain(UserJpaEntity entity);

    /**
     * OAuth 정보 매핑을 위한 헬퍼 메소드
     */
    default UserOAuth mapOAuthInfo(UserJpaEntity entity) {
        if (entity.getOauthProvider() == null && entity.getOauthId() == null) {
            return null;
        }
        
        return UserOAuth.builder()
                .provider(entity.getOauthProvider())
                .providerId(entity.getOauthId()) // oauthId -> providerId로 매핑
                .build();
    }

    /**
     * UserJpaEntity를 User 도메인으로 상세 변환
     */
    @Mapping(target = "oauthInfo", expression = "java(mapOAuthInfo(entity))")
    @Mapping(target = "isDeleted", expression = "java(entity.isDeleted())")
    User convertToUser(UserJpaEntity entity);

    /**
     * User 도메인을 UserJpaEntity로 상세 변환
     */
    @Mapping(target = "oauthProvider", expression = "java(user.getOauthInfo() != null ? user.getOauthInfo().getProvider() : null)")
    @Mapping(target = "oauthId", expression = "java(user.getOauthInfo() != null ? user.getOauthInfo().getProviderId() : null)")
    UserJpaEntity convertToJpaEntity(User user);
}