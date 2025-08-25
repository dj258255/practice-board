package io.github.beom.practiceboard.board.mapper;

import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.infrastructure.BoardJpaEntity;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardResponseDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Board 도메인과 BoardJpaEntity, DTO 간의 매핑을 담당하는 MapStruct 인터페이스
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
public interface BoardMapper {

    BoardMapper INSTANCE = Mappers.getMapper(BoardMapper.class);

    /**
     * Board 도메인 객체를 BoardJpaEntity로 변환
     */
    BoardJpaEntity toEntity(Board board);

    /**
     * BoardJpaEntity를 Board 도메인 객체로 변환
     */
    Board toDomain(BoardJpaEntity entity);

    /**
     * BoardRequestDTO를 Board 도메인 객체로 변환
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postCount", constant = "0")
    @Mapping(target = "categoryCount", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Board toDomain(BoardRequestDTO requestDTO);

    /**
     * Board 도메인 객체를 BoardResponseDTO로 변환
     */
    BoardResponseDTO toResponseDTO(Board board);

    /**
     * Board 도메인 객체의 내용을 기존 BoardJpaEntity에 업데이트
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDomain(Board board, @MappingTarget BoardJpaEntity entity);
}