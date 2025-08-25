package io.github.beom.practiceboard.board.mapper;

import io.github.beom.practiceboard.board.domain.BoardCategory;
import io.github.beom.practiceboard.board.infrastructure.BoardCategoryJpaEntity;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardCategoryRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardCategoryResponseDTO;
import org.mapstruct.*;

/**
 * BoardCategory 매퍼
 * 도메인 모델, JPA 엔티티, DTO 간의 변환을 담당합니다.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BoardCategoryMapper {
    
    /**
     * RequestDTO를 도메인 모델로 변환
     * 
     * @param requestDTO 요청 DTO
     * @return 도메인 모델
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    BoardCategory requestDtoToDomain(BoardCategoryRequestDTO requestDTO);
    
    /**
     * 도메인 모델을 ResponseDTO로 변환
     * 
     * @param domain 도메인 모델
     * @return 응답 DTO
     */
    BoardCategoryResponseDTO domainToResponseDto(BoardCategory domain);
    
    /**
     * 도메인 모델을 JPA 엔티티로 변환
     * 
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Mapping(target = "children", ignore = true)
    BoardCategoryJpaEntity domainToEntity(BoardCategory domain);
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     * 
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    @Mapping(target = "children", ignore = true)
    BoardCategory entityToDomain(BoardCategoryJpaEntity entity);
    
    /**
     * 기존 도메인 객체 업데이트 (부분 업데이트용)
     * 
     * @param requestDTO 업데이트할 데이터
     * @param target 기존 도메인 객체
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "children", ignore = true) 
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "withChildren", ignore = true)
    @Mapping(target = "deactivate", ignore = true)
    void updateDomainFromRequestDto(BoardCategoryRequestDTO requestDTO, @MappingTarget BoardCategory target);
}