package io.github.beom.practiceboard.post.infrastructure;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
// import static io.github.beom.practiceboard.post.infrastructure.QPostJpaEntity.postJpaEntity;
import io.github.beom.practiceboard.post.application.PostSearchRepository;
import io.github.beom.practiceboard.post.domain.Post;
import io.github.beom.practiceboard.post.domain.PostType;
import io.github.beom.practiceboard.post.mapper.PostMapper;
import io.github.beom.practiceboard.post.presentation.dto.response.PostPageResponseDTO;
import io.github.beom.practiceboard.post.presentation.dto.response.PostResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PostSearchRepository 인터페이스의 구현체
 * QueryDSL을 사용하여 게시글 검색 기능을 구현
 */
@Repository
@Log4j2
public class PostSearchRepositoryImpl extends QuerydslRepositorySupport implements PostSearchRepository {

    private final PostMapper postMapper;

    public PostSearchRepositoryImpl(PostMapper postMapper) {
        super(PostJpaEntity.class);
        this.postMapper = postMapper;
    }

    @Override
    public Page<PostResponseDTO> searchPosts(String[] types, String keyword, Long boardId, 
                                           Long categoryId, Long authorId, PostType postType, 
                                           Boolean pinnedOnly, Boolean featuredOnly, Pageable pageable) {
        
        log.debug("Post 검색 - types: {}, keyword: {}, boardId: {}, categoryId: {}, authorId: {}, postType: {}", 
                 types, keyword, boardId, categoryId, authorId, postType);

        QPostJpaEntity post = QPostJpaEntity.postJpaEntity;

        JPQLQuery<PostJpaEntity> query = from(post);

        // 기본 조건: 삭제되지 않은 게시글
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.deletedAt.isNull());

        // 검색 조건 추가
        if (types != null && keyword != null && !keyword.trim().isEmpty()) {
            BooleanBuilder searchBuilder = new BooleanBuilder();
            
            for (String type : types) {
                switch (type) {
                    case "t": // title
                        searchBuilder.or(post.title.containsIgnoreCase(keyword));
                        break;
                    case "c": // content
                        searchBuilder.or(post.content.containsIgnoreCase(keyword));
                        break;
                    case "a": // author
                        searchBuilder.or(post.writer.containsIgnoreCase(keyword));
                        break;
                }
            }
            builder.and(searchBuilder);
        }

        // 필터 조건들 추가
        if (boardId != null) {
            builder.and(post.boardId.eq(boardId));
        }
        if (categoryId != null) {
            builder.and(post.categoryId.eq(categoryId));
        }
        if (authorId != null) {
            builder.and(post.authorId.eq(authorId));
        }
        if (postType != null) {
            builder.and(post.postType.eq(postType));
        }
        if (Boolean.TRUE.equals(pinnedOnly)) {
            builder.and(post.isPinned.eq(true));
        }
        if (Boolean.TRUE.equals(featuredOnly)) {
            builder.and(post.isFeatured.eq(true));
        }

        query.where(builder);

        // 정렬 - 고정글이 먼저 오도록
        query.orderBy(post.isPinned.desc(), getOrderSpecifier(post, pageable));

        this.getQuerydsl().applyPagination(pageable, query);

        List<PostJpaEntity> entities = query.fetch();
        long count = query.fetchCount();

        List<PostResponseDTO> dtos = entities.stream()
                .map(entity -> {
                    Post domain = postMapper.toDomain(entity);
                    return postMapper.toResponseDTO(domain);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, count);
    }

    @Override
    public PostPageResponseDTO<PostResponseDTO> searchWithCommentCount(String[] types, String keyword, 
                                                                      Long boardId, Long categoryId, 
                                                                      Long authorId, PostType postType, 
                                                                      Pageable pageable) {
        
        Page<PostResponseDTO> page = searchPosts(types, keyword, boardId, categoryId, authorId, 
                                               postType, null, null, pageable);
        
        return PostPageResponseDTO.of(
            page.getContent(),
            pageable.getPageNumber() + 1,
            pageable.getPageSize(),
            page.getTotalElements()
        );
    }

    @Override
    public Page<PostResponseDTO> searchPopularPosts(Long boardId, Long categoryId, 
                                                   int period, Pageable pageable) {
        
        QPostJpaEntity post = QPostJpaEntity.postJpaEntity;
        JPQLQuery<PostJpaEntity> query = from(post);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.deletedAt.isNull());

        // 기간 필터링
        if (period > 0) {
            LocalDateTime fromDate = LocalDateTime.now().minusDays(period);
            builder.and(post.createdAt.goe(fromDate));
        }

        // 게시판, 카테고리 필터링
        if (boardId != null) {
            builder.and(post.boardId.eq(boardId));
        }
        if (categoryId != null) {
            builder.and(post.categoryId.eq(categoryId));
        }

        query.where(builder);
        query.orderBy(post.likeCount.desc(), post.viewCount.desc(), post.createdAt.desc());

        this.getQuerydsl().applyPagination(pageable, query);

        List<PostJpaEntity> entities = query.fetch();
        long count = query.fetchCount();

        List<PostResponseDTO> dtos = entities.stream()
                .map(entity -> {
                    Post domain = postMapper.toDomain(entity);
                    return postMapper.toResponseDTO(domain);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, count);
    }

    @Override
    public List<PostResponseDTO> searchRelatedPosts(Long postId, int limit) {
        
        QPostJpaEntity post = QPostJpaEntity.postJpaEntity;
        QPostJpaEntity targetPost = new QPostJpaEntity("targetPost");

        // 먼저 대상 게시글의 정보를 조회
        PostJpaEntity target = from(targetPost)
                .where(targetPost.id.eq(postId).and(targetPost.deletedAt.isNull()))
                .fetchOne();

        if (target == null) {
            return List.of();
        }

        JPQLQuery<PostJpaEntity> query = from(post);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.deletedAt.isNull());
        builder.and(post.id.ne(postId)); // 자기 자신 제외

        // 같은 카테고리의 게시글 우선
        if (target.getCategoryId() != null) {
            builder.and(post.categoryId.eq(target.getCategoryId()));
        } else {
            builder.and(post.boardId.eq(target.getBoardId())); // 같은 게시판
        }

        query.where(builder);
        query.orderBy(post.likeCount.desc(), post.viewCount.desc(), post.createdAt.desc());
        query.limit(limit);

        List<PostJpaEntity> entities = query.fetch();

        return entities.stream()
                .map(entity -> {
                    Post domain = postMapper.toDomain(entity);
                    return postMapper.toResponseDTO(domain);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PostPageResponseDTO<PostResponseDTO> searchWithStatistics(String[] types, String keyword, 
                                                                    Long boardId, Long categoryId, 
                                                                    Pageable pageable) {
        
        Page<PostResponseDTO> page = searchPosts(types, keyword, boardId, categoryId, 
                                               null, null, null, null, pageable);
        
        return PostPageResponseDTO.of(
            page.getContent(),
            pageable.getPageNumber() + 1,
            pageable.getPageSize(),
            page.getTotalElements()
        );
    }

    /**
     * 정렬 조건에 따른 OrderSpecifier 생성
     */
    private OrderSpecifier<?> getOrderSpecifier(QPostJpaEntity post, Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return post.createdAt.desc(); // 기본 정렬
        }

        return pageable.getSort().stream()
                .findFirst()
                .map(order -> {
                    boolean isAsc = order.getDirection().isAscending();
                    switch (order.getProperty()) {
                        case "createdAt":
                            return isAsc ? post.createdAt.asc() : post.createdAt.desc();
                        case "viewCount":
                            return isAsc ? post.viewCount.asc() : post.viewCount.desc();
                        case "likeCount":
                            return isAsc ? post.likeCount.asc() : post.likeCount.desc();
                        case "title":
                            return isAsc ? post.title.asc() : post.title.desc();
                        default:
                            return post.createdAt.desc();
                    }
                })
                .orElse(post.createdAt.desc());
    }
}