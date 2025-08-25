package io.github.beom.practiceboard.comment.infrastructure;

import io.github.beom.practiceboard.comment.application.CommentRepository;
import io.github.beom.practiceboard.comment.domain.Comment;
import io.github.beom.practiceboard.comment.mapper.CommentMapper;
import io.github.beom.practiceboard.comment.presentation.dto.request.CommentPageRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.response.CommentResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 댓글 리포지토리 구현체
 * 댓글 데이터 접근 로직을 구현합니다.
 */
@Repository
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentMapper commentMapper;

    /**
     * 댓글 등록
     */
    @Override
    public Long register(Comment comment) {
        log.debug("댓글 등록: {}", comment);
        
        CommentJpaEntity entity = commentMapper.fromDomain(comment);
        CommentJpaEntity savedEntity = commentJpaRepository.save(entity);
        
        log.debug("댓글 등록 완료: ID = {}", savedEntity.getId());
        return savedEntity.getId();
    }

    /**
     * 댓글 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Comment read(Long commentId) {
        log.debug("댓글 조회: ID = {}", commentId);
        
        Optional<CommentJpaEntity> entityOpt = commentJpaRepository.findById(commentId);
        if (entityOpt.isEmpty()) {
            log.debug("댓글을 찾을 수 없음: ID = {}", commentId);
            return null;
        }
        
        Comment comment = commentMapper.toDomain(entityOpt.get());
        log.debug("댓글 조회 완료: {}", comment);
        return comment;
    }

    /**
     * 댓글 수정
     */
    @Override
    public void modify(Comment comment) {
        log.debug("댓글 수정: {}", comment);
        
        Optional<CommentJpaEntity> entityOpt = commentJpaRepository.findById(comment.getId());
        if (entityOpt.isEmpty()) {
            throw new IllegalArgumentException("수정할 댓글이 존재하지 않습니다: " + comment.getId());
        }
        
        CommentJpaEntity entity = entityOpt.get();
        commentMapper.updateFromDomain(comment, entity);
        commentJpaRepository.save(entity);
        
        log.debug("댓글 수정 완료: ID = {}", comment.getId());
    }

    /**
     * 댓글 삭제 (물리적 삭제)
     */
    @Override
    public void remove(Long commentId) {
        log.debug("댓글 삭제: ID = {}", commentId);
        
        // 먼저 대댓글들을 모두 삭제
        commentJpaRepository.deleteByParentCommentId(commentId);
        
        // 그 다음 해당 댓글 삭제
        commentJpaRepository.deleteById(commentId);
        
        log.debug("댓글 삭제 완료: ID = {}", commentId);
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO<CommentResponseDTO> getListOfBoard(Long boardId, CommentPageRequestDTO pageRequestDTO) {
        log.debug("게시글 {}의 댓글 목록 조회: {}", boardId, pageRequestDTO);
        
        Pageable pageable = pageRequestDTO.getPageable("createdAt");
        Page<CommentJpaEntity> entityPage;
        
        // 검색 조건에 따른 쿼리 실행
        if (pageRequestDTO.getKeyword() != null && !pageRequestDTO.getKeyword().trim().isEmpty()) {
            entityPage = executeSearchQuery(boardId, pageRequestDTO, pageable);
        } else if (pageRequestDTO.getDepth() != null) {
            entityPage = commentJpaRepository.findByBoardIdAndDepth(boardId, pageRequestDTO.getDepth(), pageable);
        } else {
            entityPage = commentJpaRepository.findByBoardId(boardId, pageable);
        }
        
        List<CommentResponseDTO> dtoList = entityPage.getContent().stream()
                .map(commentMapper::entityToResponseDTO)
                .collect(Collectors.toList());
        
        return BoardPageResponseDTO.<CommentResponseDTO>of()
                .page(pageRequestDTO.getPage())
                .size(pageRequestDTO.getSize())
                .dtoList(dtoList)
                .total((int) entityPage.getTotalElements())
                .build();
    }
    
    /**
     * 특정 게시글의 계층형 댓글 목록 조회 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO<CommentResponseDTO> getHierarchicalListOfBoard(Long boardId, CommentPageRequestDTO pageRequestDTO) {
        log.debug("게시글 {}의 계층형 댓글 목록 조회: {}", boardId, pageRequestDTO);
        
        // 최상위 댓글만 페이징 조회
        Pageable pageable = pageRequestDTO.getPageable("createdAt");
        Page<CommentJpaEntity> rootCommentPage = commentJpaRepository.findRootCommentsByBoardId(boardId, pageable);
        
        // 각 최상위 댓글의 대댓글들을 조회하여 계층구조 구성
        List<CommentResponseDTO> hierarchicalList = rootCommentPage.getContent().stream()
                .map(rootEntity -> {
                    CommentResponseDTO rootDto = commentMapper.entityToResponseDTO(rootEntity);
                    
                    // 해당 댓글의 대댓글들 조회
                    List<CommentJpaEntity> childEntities = commentJpaRepository.findByParentCommentId(rootEntity.getId());
                    List<CommentResponseDTO> childDtos = childEntities.stream()
                            .map(commentMapper::entityToResponseDTO)
                            .collect(Collectors.toList());
                    
                    rootDto.setChildren(childDtos);
                    return rootDto;
                })
                .collect(Collectors.toList());
        
        return BoardPageResponseDTO.<CommentResponseDTO>of()
                .page(pageRequestDTO.getPage())
                .size(pageRequestDTO.getSize())
                .dtoList(hierarchicalList)
                .total((int) rootCommentPage.getTotalElements())
                .build();
    }
    
    /**
     * 특정 부모 댓글의 대댓글 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getChildComments(Long parentId) {
        log.debug("부모 댓글 {}의 대댓글 목록 조회", parentId);
        
        List<CommentJpaEntity> childEntities = commentJpaRepository.findByParentCommentId(parentId);
        
        return childEntities.stream()
                .map(commentMapper::entityToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 댓글이 대댓글을 가지고 있는지 확인
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasChildComments(Long commentId) {
        log.debug("댓글 {}의 대댓글 존재 여부 확인", commentId);
        
        return commentJpaRepository.hasChildComments(commentId);
    }

    /**
     * 특정 게시글의 모든 댓글 삭제
     */
    @Override
    public void deleteByBoardId(Long boardId) {
        log.debug("게시글 {}의 모든 댓글 삭제", boardId);
        
        commentJpaRepository.deleteByBoardId(boardId);
        
        log.debug("게시글 {}의 모든 댓글 삭제 완료", boardId);
    }

    /**
     * 특정 게시글의 댓글 개수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public long countByBoardId(Long boardId) {
        log.debug("게시글 {}의 댓글 개수 조회", boardId);
        
        return commentJpaRepository.countByBoardId(boardId);
    }

    /**
     * 특정 부모 댓글의 대댓글 개수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public long countByParentId(Long parentId) {
        log.debug("부모 댓글 {}의 대댓글 개수 조회", parentId);
        
        return commentJpaRepository.countByParentCommentId(parentId);
    }

    /**
     * 검색 조건에 따른 쿼리 실행
     */
    private Page<CommentJpaEntity> executeSearchQuery(Long boardId, CommentPageRequestDTO pageRequestDTO, Pageable pageable) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        
        if (types == null || types.length == 0) {
            return commentJpaRepository.findByBoardId(boardId, pageable);
        }
        
        boolean hasContent = false;
        boolean hasWriter = false;
        
        for (String type : types) {
            switch (type) {
                case "c":
                    hasContent = true;
                    break;
                case "w":
                    hasWriter = true;
                    break;
            }
        }
        
        if (hasContent && hasWriter) {
            return commentJpaRepository.findByBoardIdAndContentOrReplayer(boardId, keyword, pageable);
        } else if (hasContent) {
            return commentJpaRepository.findByBoardIdAndReplyTextContaining(boardId, keyword, pageable);
        } else if (hasWriter) {
            return commentJpaRepository.findByBoardIdAndReplayerContaining(boardId, keyword, pageable);
        } else {
            return commentJpaRepository.findByBoardId(boardId, pageable);
        }
    }

}