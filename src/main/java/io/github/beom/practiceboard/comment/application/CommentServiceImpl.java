package io.github.beom.practiceboard.comment.application;

import io.github.beom.practiceboard.comment.domain.Comment;
import io.github.beom.practiceboard.comment.event.CommentCreatedEvent;
import io.github.beom.practiceboard.comment.event.CommentDeletedEvent;
import io.github.beom.practiceboard.comment.event.CommentUpdatedEvent;
import io.github.beom.practiceboard.comment.presentation.CommentService;
import io.github.beom.practiceboard.global.event.EventPublisher;
import io.github.beom.practiceboard.comment.presentation.dto.request.CommentPageRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.request.CommentRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.response.CommentResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 서비스 구현체
 * 댓글 비즈니스 로직을 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventPublisher eventPublisher;

    /**
     * 댓글 등록
     * @param requestDTO 등록할 댓글 정보
     * @return 등록된 댓글의 ID
     */
    @Override
    public Long register(CommentRequestDTO requestDTO) {
        log.info("댓글 등록: {}", requestDTO);
        
        // 유효성 검사
        validateCommentRequest(requestDTO);

        // 부모 댓글 검증 (대댓글인 경우)
        if (requestDTO.isChildReply()) {
            validateParentComment(requestDTO);
        }

        // DTO를 도메인 객체로 변환
        Comment comment = convertToComment(requestDTO);
        
        Long commentId = commentRepository.register(comment);
        
        // 댓글 생성 이벤트 발행
        publishCommentCreatedEvent(comment.toBuilder().id(commentId).build());
        
        return commentId;
    }

    /**
     * 댓글 조회
     * @param commentId 조회할 댓글 ID
     * @return 조회된 댓글 정보
     */
    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO read(Long commentId) {
        log.info("댓글 조회: {}", commentId);
        
        Comment comment = commentRepository.read(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId);
        }
        
        return convertToResponseDTO(comment);
    }

    /**
     * 댓글 수정
     * @param commentId 수정할 댓글 ID
     * @param requestDTO 수정할 댓글 정보
     */
    @Override
    public void modify(Long commentId, CommentRequestDTO requestDTO) {
        log.info("댓글 수정: {}, {}", commentId, requestDTO);
        
        // 댓글 존재 여부 확인
        Comment existingComment = commentRepository.read(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("수정할 댓글이 존재하지 않습니다: " + commentId);
        }
        
        // 유효성 검사
        validateCommentRequest(requestDTO);
        
        // 수정할 댓글 객체 생성
        Comment updatedComment = Comment.builder()
                .id(commentId)
                .postId(requestDTO.getPostId())
                .content(requestDTO.getContent())
                .authorId(requestDTO.getAuthorId())
                .parentReplyId(requestDTO.getParentReplyId())
                .depth(requestDTO.getDepth())
                .build();
        
        commentRepository.modify(updatedComment);
        
        // 댓글 수정 이벤트 발행
        publishCommentUpdatedEvent(updatedComment);
    }

    /**
     * 댓글 삭제
     * @param commentId 삭제할 댓글 ID
     */
    @Override
    public void remove(Long commentId) {
        log.info("댓글 삭제: {}", commentId);
        
        // 댓글 존재 여부 확인
        Comment existingComment = commentRepository.read(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("삭제할 댓글이 존재하지 않습니다: " + commentId);
        }
        
        // 대댓글이 있는 경우 삭제 불가 (선택사항)
        if (hasChildComments(commentId)) {
            throw new IllegalStateException("대댓글이 있는 댓글은 삭제할 수 없습니다: " + commentId);
        }
        
        // 삭제 이벤트 발행을 위해 삭제 전 데이터 보관
        publishCommentDeletedEvent(existingComment);
        
        commentRepository.remove(commentId);
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO<CommentResponseDTO> getListOfBoard(Long boardId, CommentPageRequestDTO pageRequestDTO) {
        log.info("게시글 {}의 댓글 목록 조회, 페이지: {}", boardId, pageRequestDTO);
        return commentRepository.getListOfBoard(boardId, pageRequestDTO);
    }
    
    /**
     * 특정 게시글의 계층형 댓글 목록 조회 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public BoardPageResponseDTO<CommentResponseDTO> getHierarchicalListOfBoard(Long boardId, CommentPageRequestDTO pageRequestDTO) {
        log.info("게시글 {}의 계층형 댓글 목록 조회, 페이지: {}", boardId, pageRequestDTO);
        return commentRepository.getHierarchicalListOfBoard(boardId, pageRequestDTO);
    }
    
    /**
     * 특정 부모 댓글의 대댓글 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getChildComments(Long parentId) {
        log.info("부모 댓글 {}의 대댓글 목록 조회", parentId);
        return commentRepository.getChildComments(parentId);
    }
    
    /**
     * 특정 댓글이 대댓글을 가지고 있는지 확인
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasChildComments(Long commentId) {
        log.info("댓글 {}의 대댓글 존재 여부 확인", commentId);
        return commentRepository.hasChildComments(commentId);
    }

    /**
     * 특정 게시글의 댓글 개수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public long countByBoardId(Long boardId) {
        log.info("게시글 {}의 댓글 개수 조회", boardId);
        return commentRepository.countByBoardId(boardId);
    }

    /**
     * 특정 부모 댓글의 대댓글 개수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public long countByParentId(Long parentId) {
        log.info("부모 댓글 {}의 대댓글 개수 조회", parentId);
        return commentRepository.countByParentId(parentId);
    }

    /**
     * 댓글 요청 유효성 검사
     */
    private void validateCommentRequest(CommentRequestDTO requestDTO) {
        if (requestDTO.getContent() == null || requestDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }

        if (requestDTO.getContent().trim().length() > 255) {
            throw new IllegalArgumentException("댓글 내용은 255자를 초과할 수 없습니다.");
        }

        if (requestDTO.getPostId() == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다.");
        }

        if (requestDTO.getAuthorId() == null) {
            throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        }
    }

    /**
     * 부모 댓글 유효성 검사
     */
    private void validateParentComment(CommentRequestDTO requestDTO) {
        Comment parentComment = commentRepository.read(requestDTO.getParentReplyId());
        if (parentComment == null) {
            throw new IllegalArgumentException("부모 댓글이 존재하지 않습니다: " + requestDTO.getParentReplyId());
        }

        // 부모 댓글이 이미 대댓글인 경우 (최대 1계층까지만 허용)
        if (parentComment.getDepth() > 0) {
            throw new IllegalArgumentException("대댓글에는 댓글을 달 수 없습니다. 최대 1계층까지만 허용됩니다.");
        }

        // 다른 게시글의 댓글에 대한 대댓글 방지
        if (!parentComment.getPostId().equals(requestDTO.getPostId())) {
            throw new IllegalArgumentException("다른 게시글에 달려있는 댓글에 대댓글을 달 수 없습니다.");
        }

        // 대댓글 깊이 설정
        requestDTO.setDepth(1);
    }

    /**
     * RequestDTO를 Comment 도메인 객체로 변환
     */
    private Comment convertToComment(CommentRequestDTO requestDTO) {
        return Comment.builder()
                .postId(requestDTO.getPostId())
                .content(requestDTO.getContent())
                .authorId(requestDTO.getAuthorId())
                .parentReplyId(requestDTO.getParentReplyId())
                .depth(requestDTO.getDepth())
                .build();
    }

    /**
     * Comment 도메인 객체를 ResponseDTO로 변환
     */
    private CommentResponseDTO convertToResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .boardId(comment.getBoardId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .parentReplyId(comment.getParentReplyId())
                .depth(comment.getDepth())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /**
     * 댓글 생성 이벤트 발행
     */
    private void publishCommentCreatedEvent(Comment comment) {
        try {
            CommentCreatedEvent event = CommentCreatedEvent.of(
                comment.getId(),
                comment.getBoardId(),
                comment.getContent(),
                comment.getAuthorId().toString(),
                comment.getParentReplyId(),
                comment.getDepth()
            );
            
            eventPublisher.publishCommentEvent(event);
            log.debug("댓글 생성 이벤트 발행: commentId={}", comment.getId());
            
        } catch (Exception e) {
            log.error("댓글 생성 이벤트 발행 실패: commentId={}, error={}", comment.getId(), e.getMessage());
            // 이벤트 발행 실패가 주요 로직에 영향을 주지 않도록 예외를 다시 던지지 않음
        }
    }

    /**
     * 댓글 수정 이벤트 발행
     */
    private void publishCommentUpdatedEvent(Comment comment) {
        try {
            CommentUpdatedEvent event = CommentUpdatedEvent.of(
                comment.getId(),
                comment.getBoardId(),
                comment.getContent(), // oldContent
                comment.getContent(), // newContent (수정된 내용)
                comment.getAuthorId().toString()
            );
            
            eventPublisher.publishCommentEvent(event);
            log.debug("댓글 수정 이벤트 발행: commentId={}", comment.getId());
            
        } catch (Exception e) {
            log.error("댓글 수정 이벤트 발행 실패: commentId={}, error={}", comment.getId(), e.getMessage());
        }
    }

    /**
     * 댓글 삭제 이벤트 발행
     */
    private void publishCommentDeletedEvent(Comment comment) {
        try {
            CommentDeletedEvent event = CommentDeletedEvent.of(
                comment.getId(),
                comment.getBoardId(),
                comment.getContent(),
                comment.getAuthorId().toString(),
                comment.getParentReplyId(),
                hasChildComments(comment.getId())
            );
            
            eventPublisher.publishCommentEvent(event);
            log.debug("댓글 삭제 이벤트 발행: commentId={}", comment.getId());
            
        } catch (Exception e) {
            log.error("댓글 삭제 이벤트 발행 실패: commentId={}, error={}", comment.getId(), e.getMessage());
        }
    }
}