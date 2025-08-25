package io.github.beom.practiceboard.board.infrastructure;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import io.github.beom.practiceboard.board.application.BoardSearchRepository;
import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.mapper.BoardMapper;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BoardSearchRepository 인터페이스의 구현체
 * QueryDSL을 사용하여 게시판 검색 기능을 구현
 */
@Repository
@Log4j2
public class BoardSearchRepositoryImpl extends QuerydslRepositorySupport implements BoardSearchRepository {

    private final BoardMapper boardMapper;

    public BoardSearchRepositoryImpl(BoardMapper boardMapper) {
        super(BoardJpaEntity.class);
        this.boardMapper = boardMapper;
    }

    @Override
    public Page<BoardResponseDTO> searchBoards(String[] types, String keyword, BoardType boardType, 
                                              BoardStatus status, Pageable pageable) {
        
        log.debug("게시판 검색 - types: {}, keyword: {}, boardType: {}, status: {}", 
                 types, keyword, boardType, status);

        QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;
        JPQLQuery<BoardJpaEntity> query = from(board);

        // 기본 조건: 삭제되지 않은 게시판
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.deletedAt.isNull());

        // 검색 조건 추가
        if (types != null && keyword != null && !keyword.trim().isEmpty()) {
            BooleanBuilder searchBuilder = new BooleanBuilder();
            
            for (String type : types) {
                switch (type) {
                    case "n": // name
                        searchBuilder.or(board.name.containsIgnoreCase(keyword));
                        break;
                    case "d": // description
                        searchBuilder.or(board.description.containsIgnoreCase(keyword));
                        break;
                    // slug 필드가 제거되어 주석 처리
                    // case "s": // slug
                    //     searchBuilder.or(board.slug.containsIgnoreCase(keyword));
                    //     break;
                }
            }
            builder.and(searchBuilder);
        }

        // 필터 조건들 추가
        if (boardType != null) {
            builder.and(board.boardType.eq(boardType));
        }
        if (status != null) {
            builder.and(board.status.eq(status));
        }

        query.where(builder);
        query.orderBy(getOrderSpecifier(board, pageable));

        this.getQuerydsl().applyPagination(pageable, query);

        List<BoardJpaEntity> entities = query.fetch();
        long count = query.fetchCount();

        List<BoardResponseDTO> dtos = entities.stream()
                .map(entity -> {
                    Board domain = entity.toDomain();
                    return boardMapper.toResponseDTO(domain);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, count);
    }

    @Override
    public Page<BoardResponseDTO> searchActiveBoards(String[] types, String keyword, 
                                                    BoardType boardType, Pageable pageable) {
        
        return searchBoards(types, keyword, boardType, BoardStatus.ACTIVE, pageable);
    }

    @Override
    public BoardPageResponseDTO<BoardResponseDTO> searchWithStatistics(String[] types, String keyword, 
                                                                      BoardType boardType, Pageable pageable) {
        
        Page<BoardResponseDTO> page = searchBoards(types, keyword, boardType, null, pageable);
        
        return BoardPageResponseDTO.of(
            page.getContent(),
            pageable.getPageNumber() + 1,
            pageable.getPageSize(),
            page.getTotalElements()
        );
    }

    @Override
    public Page<BoardResponseDTO> searchPopularBoards(int limit, Pageable pageable) {
        
        QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;
        JPQLQuery<BoardJpaEntity> query = from(board);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.deletedAt.isNull());
        builder.and(board.status.eq(BoardStatus.ACTIVE));

        query.where(builder);
        query.orderBy(board.postCount.desc(), board.categoryCount.desc(), board.createdAt.desc());

        this.getQuerydsl().applyPagination(pageable, query);

        List<BoardJpaEntity> entities = query.fetch();
        long count = query.fetchCount();

        List<BoardResponseDTO> dtos = entities.stream()
                .map(entity -> {
                    Board domain = entity.toDomain();
                    return boardMapper.toResponseDTO(domain);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, count);
    }

    @Override
    public Page<BoardResponseDTO> searchBoardsByManager(Long managerId, Pageable pageable) {
        
        QBoardJpaEntity board = QBoardJpaEntity.boardJpaEntity;
        JPQLQuery<BoardJpaEntity> query = from(board);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(board.deletedAt.isNull());
        builder.and(board.managerId.eq(managerId));

        query.where(builder);
        query.orderBy(board.createdAt.desc());

        this.getQuerydsl().applyPagination(pageable, query);

        List<BoardJpaEntity> entities = query.fetch();
        long count = query.fetchCount();

        List<BoardResponseDTO> dtos = entities.stream()
                .map(entity -> {
                    Board domain = entity.toDomain();
                    return boardMapper.toResponseDTO(domain);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, count);
    }

    /**
     * 정렬 조건에 따른 OrderSpecifier 생성
     */
    private OrderSpecifier<?> getOrderSpecifier(QBoardJpaEntity board, Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return board.createdAt.desc(); // 기본 정렬
        }

        return pageable.getSort().stream()
                .findFirst()
                .map(order -> {
                    boolean isAsc = order.getDirection().isAscending();
                    switch (order.getProperty()) {
                        case "createdAt":
                            return isAsc ? board.createdAt.asc() : board.createdAt.desc();
                        case "name":
                            return isAsc ? board.name.asc() : board.name.desc();
                        case "postCount":
                            return isAsc ? board.postCount.asc() : board.postCount.desc();
                        case "categoryCount":
                            return isAsc ? board.categoryCount.asc() : board.categoryCount.desc();
                        default:
                            return board.createdAt.desc();
                    }
                })
                .orElse(board.createdAt.desc());
    }
}