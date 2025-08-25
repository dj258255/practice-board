package io.github.beom.practiceboard.board.infrastructure;

import io.github.beom.practiceboard.board.application.BoardRepository;
import io.github.beom.practiceboard.board.domain.Board;
import io.github.beom.practiceboard.board.domain.BoardStatus;
import io.github.beom.practiceboard.board.domain.BoardType;
import io.github.beom.practiceboard.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 게시판 Repository 구현체
 * 게시판 도메인과 JPA 인프라스트럭처 계층을 연결합니다.
 */
@Repository
@RequiredArgsConstructor
@Log4j2
@Transactional
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository boardJpaRepository;
    private final BoardMapper boardMapper;

    @Override
    public Long save(Board board) {
        log.debug("게시판 저장: {}", board.getName());

        BoardJpaEntity entity;

        if (board.getId() != null) {
            // 기존 게시판 수정
            Optional<BoardJpaEntity> existingEntity = boardJpaRepository.findById(board.getId());
            if (existingEntity.isPresent()) {
                entity = existingEntity.get();
                // 도메인 정보를 JPA 엔티티에 업데이트
                boardMapper.updateEntityFromDomain(board, entity);
            } else {
                throw new IllegalArgumentException("수정할 게시판이 존재하지 않습니다: " + board.getId());
            }
        } else {
            // 새 게시판 생성
            entity = boardMapper.toEntity(board);
        }

        BoardJpaEntity savedEntity = boardJpaRepository.save(entity);
        return savedEntity.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Board> findById(Long id) {
        log.debug("게시판 ID 조회: {}", id);
        
        return boardJpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(BoardJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findAllActive() {
        log.debug("활성 게시판 목록 조회");
        
        return boardJpaRepository.findByStatusAndDeletedAtIsNull(BoardStatus.ACTIVE)
                .stream()
                .map(BoardJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findByType(BoardType boardType) {
        log.debug("게시판 타입별 조회: {}", boardType);
        
        return boardJpaRepository.findByBoardTypeAndDeletedAtIsNull(boardType)
                .stream()
                .map(BoardJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findByStatus(BoardStatus status) {
        log.debug("게시판 상태별 조회: {}", status);
        
        return boardJpaRepository.findByStatusAndDeletedAtIsNull(status)
                .stream()
                .map(BoardJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findByManagerId(Long managerId) {
        log.debug("관리자별 게시판 조회: {}", managerId);
        
        return boardJpaRepository.findByManagerIdAndDeletedAtIsNull(managerId)
                .stream()
                .map(BoardJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        log.debug("게시판 삭제: {}", id);
        
        Optional<BoardJpaEntity> entityOptional = boardJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            BoardJpaEntity entity = entityOptional.get();
            // BaseAllEntity의 소프트 삭제 기능 사용
            entity.markDeleted(); // deletedAt 설정
            boardJpaRepository.save(entity);
        } else {
            throw new IllegalArgumentException("삭제할 게시판이 존재하지 않습니다: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return boardJpaRepository.findByIdAndDeletedAtIsNull(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return boardJpaRepository.countByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        return boardJpaRepository.countByStatusAndDeletedAtIsNull(BoardStatus.ACTIVE);
    }
}