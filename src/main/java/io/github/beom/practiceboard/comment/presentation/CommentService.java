package io.github.beom.practiceboard.comment.presentation;

import io.github.beom.practiceboard.comment.presentation.dto.request.CommentPageRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.request.CommentRequestDTO;
import io.github.beom.practiceboard.comment.presentation.dto.response.CommentResponseDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardPageResponseDTO;

import java.util.List;

/**
 * 댓글 서비스 인터페이스
 * 댓글 비즈니스 로직을 위한 계약을 정의합니다.
 */
public interface CommentService {

    /**
     * 댓글 등록
     * @param requestDTO 등록할 댓글 정보
     * @return 등록된 댓글의 ID
     */
    Long register(CommentRequestDTO requestDTO);

    /**
     * 댓글 조회
     * @param commentId 조회할 댓글 ID
     * @return 조회된 댓글 정보
     */
    CommentResponseDTO read(Long commentId);

    /**
     * 댓글 수정
     * @param commentId 수정할 댓글 ID
     * @param requestDTO 수정할 댓글 정보
     */
    void modify(Long commentId, CommentRequestDTO requestDTO);

    /**
     * 댓글 삭제
     * @param commentId 삭제할 댓글 ID
     */
    void remove(Long commentId);

    /**
     * 특정 게시글의 댓글 목록 조회 (페이징)
     * @param boardId 게시글 ID
     * @param pageRequestDTO 페이지 요청 정보
     * @return 페이징된 댓글 목록
     */
    BoardPageResponseDTO<CommentResponseDTO> getListOfBoard(Long boardId, CommentPageRequestDTO pageRequestDTO);
    
    /**
     * 특정 게시글의 계층형 댓글 목록 조회 (페이징)
     * 최상위 댓글만 페이징하고, 각 최상위 댓글의 대댓글은 모두 포함
     * @param boardId 게시글 ID
     * @param pageRequestDTO 페이지 요청 정보
     * @return 페이징된 계층형 댓글 목록
     */
    BoardPageResponseDTO<CommentResponseDTO> getHierarchicalListOfBoard(Long boardId, CommentPageRequestDTO pageRequestDTO);
    
    /**
     * 특정 부모 댓글의 대댓글 목록 조회
     * @param parentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    List<CommentResponseDTO> getChildComments(Long parentId);
    
    /**
     * 특정 댓글이 대댓글을 가지고 있는지 확인
     * @param commentId 댓글 ID
     * @return 대댓글 존재 여부
     */
    boolean hasChildComments(Long commentId);

    /**
     * 특정 게시글의 댓글 개수 조회
     * @param boardId 게시글 ID
     * @return 댓글 개수
     */
    long countByBoardId(Long boardId);

    /**
     * 특정 부모 댓글의 대댓글 개수 조회
     * @param parentId 부모 댓글 ID
     * @return 대댓글 개수
     */
    long countByParentId(Long parentId);
}