package io.github.beom.practiceboard.favorite.mapper;

import io.github.beom.practiceboard.favorite.domain.Favorite;
import io.github.beom.practiceboard.favorite.infrastructure.FavoriteJpaEntity;
import org.mapstruct.*;

/**
 * Favorite 매퍼
 * 도메인 모델과 JPA 엔티티 간의 변환을 담당합니다.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FavoriteMapper {
    
    /**
     * 도메인 모델을 JPA 엔티티로 변환
     * 
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FavoriteJpaEntity domainToEntity(Favorite domain);
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     * 
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    Favorite entityToDomain(FavoriteJpaEntity entity);
}