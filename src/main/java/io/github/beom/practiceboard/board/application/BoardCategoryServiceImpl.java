package io.github.beom.practiceboard.board.application;

import io.github.beom.practiceboard.board.domain.BoardCategory;
import io.github.beom.practiceboard.board.mapper.BoardCategoryMapper;
import io.github.beom.practiceboard.board.presentation.BoardCategoryService;
import io.github.beom.practiceboard.board.presentation.dto.request.BoardCategoryRequestDTO;
import io.github.beom.practiceboard.board.presentation.dto.response.BoardCategoryResponseDTO;
import io.github.beom.practiceboard.board.exception.BoardCategoryNotFoundException;
import io.github.beom.practiceboard.board.exception.CategoryHasChildrenException;
import io.github.beom.practiceboard.board.exception.CircularReferenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardCategoryServiceImpl implements BoardCategoryService {
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardCategoryMapper boardCategoryMapper;

    /**
     * 카테고리 등록
     */
    @Override
    public Long register(BoardCategoryRequestDTO boardCategoryRequestDTO){
        try {
            log.info("카테고리 등록 시작: {}", boardCategoryRequestDTO);
            BoardCategory category = boardCategoryMapper.requestDtoToDomain(boardCategoryRequestDTO);
            Long savedId = boardCategoryRepository.save(category);
            log.info("카테고리 등록 완료: ID={}", savedId);
            return savedId;
        } catch (Exception e) {
            log.error("카테고리 등록 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
    /**
     * 카테고리 조회
     */
    @Override
    @Transactional(readOnly = true)
    public BoardCategoryResponseDTO readOne(Long id){
        try {
            log.info("카테고리 조회 시작: ID={}", id);
            Optional<BoardCategory> result = boardCategoryRepository.findById(id);
            BoardCategory category = result.orElseThrow(() ->
                    new BoardCategoryNotFoundException(id));
            BoardCategoryResponseDTO response = boardCategoryMapper.domainToResponseDto(category);
            log.info("카테고리 조회 완료: {}", response);
            return response;
        } catch (BoardCategoryNotFoundException e) {
            log.error("카테고리를 찾을 수 없음: ID={}", id);
            throw e;
        } catch (Exception e) {
            log.error("카테고리 조회 실패: ID={}, 오류={}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 카테고리 수정
     */
    @Override
    public void modify(BoardCategoryRequestDTO boardCategoryRequestDTO) {
        try {
            log.info("카테고리 수정 시작: {}", boardCategoryRequestDTO);
            Optional<BoardCategory> result = boardCategoryRepository.findById(boardCategoryRequestDTO.getId());
            BoardCategory category = result.orElseThrow(() ->
                    new BoardCategoryNotFoundException(boardCategoryRequestDTO.getId()));

            // 카테고리 이름 변경
            if (!category.getCategoryName().equals(boardCategoryRequestDTO.getCategoryName())) {
                category = category.changeName(boardCategoryRequestDTO.getCategoryName(), getCurrentUserId());
            }

            // 추천 게시글 기준값 변경
            if (category.getRecommendThreshold() != boardCategoryRequestDTO.getRecommendThreshold()) {
                category = category.changeRecommendThreshold(boardCategoryRequestDTO.getRecommendThreshold(), getCurrentUserId());
            }

            // 활성화 상태 변경
            if (category.isActive() != boardCategoryRequestDTO.isActive()) {
                if (!boardCategoryRequestDTO.isActive()) {
                    category = category.deactivate(getCurrentUserId());
                } else {
                    // 활성화 로직 (현재 도메인 모델에 없으므로 새로 생성)
                    category = BoardCategory.builder()
                            .id(category.getId())
                            .categoryName(category.getCategoryName())
                            .description(boardCategoryRequestDTO.getDescription())
                            .boardId(category.getBoardId())
                            .createdBy(category.getCreatedBy())
                            .updatedBy(getCurrentUserId())
                            .parentId(category.getParentId())
                            .isActive(true)
                            .sortOrder(category.getSortOrder())
                            .recommendThreshold(category.getRecommendThreshold())
                            .createdAt(category.getCreatedAt())
                            .updatedAt(java.time.LocalDateTime.now())
                            .children(category.getChildren())
                            .build();
                }
            }

            // 저장
            boardCategoryRepository.save(category);
            log.info("카테고리 수정 완료: ID={}", boardCategoryRequestDTO.getId());
        } catch (BoardCategoryNotFoundException e) {
            log.error("카테고리를 찾을 수 없음: ID={}", boardCategoryRequestDTO.getId());
            throw e;
        } catch (Exception e) {
            log.error("카테고리 수정 실패: ID={}, 오류={}", boardCategoryRequestDTO.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 카테고리 삭제
     */
    @Override
    public void remove(Long id) {
        try {
            log.info("카테고리 삭제 시작: ID={}", id);
            
            // 카테고리 존재 확인
            Optional<BoardCategory> result = boardCategoryRepository.findById(id);
            if (result.isEmpty()) {
                throw new BoardCategoryNotFoundException(id);
            }
            
            // 하위 카테고리가 있는지 확인
            List<BoardCategory> subCategories = boardCategoryRepository.findByParentId(id);
            if (!subCategories.isEmpty()) {
                throw new CategoryHasChildrenException(id);
            }

            boardCategoryRepository.deleteById(id);
            log.info("카테고리 삭제 완료: ID={}", id);
        } catch (BoardCategoryNotFoundException | CategoryHasChildrenException e) {
            log.error("카테고리 삭제 실패: ID={}, 오류={}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("카테고리 삭제 중 오류 발생: ID={}, 오류={}", id, e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 루트 카테고리 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<BoardCategoryResponseDTO> getRootCategories(Long boardId) {
        try {
            log.info("루트 카테고리 목록 조회 시작: boardId={}", boardId);
            List<BoardCategory> rootCategories = boardCategoryRepository.findRootCategories(boardId);
            List<BoardCategoryResponseDTO> response = rootCategories.stream()
                    .map(boardCategoryMapper::domainToResponseDto)
                    .collect(Collectors.toList());
            log.info("루트 카테고리 목록 조회 완료: {} 개", response.size());
            return response;
        } catch (Exception e) {
            log.error("루트 카테고리 목록 조회 실패: boardId={}, 오류={}", boardId, e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 하위 카테고리 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<BoardCategoryResponseDTO> getSubCategories(Long parentId) {
        try {
            log.info("하위 카테고리 목록 조회 시작: parentId={}", parentId);
            List<BoardCategory> subCategories = boardCategoryRepository.findByParentId(parentId);
            List<BoardCategoryResponseDTO> response = subCategories.stream()
                    .map(boardCategoryMapper::domainToResponseDto)
                    .collect(Collectors.toList());
            log.info("하위 카테고리 목록 조회 완료: {} 개", response.size());
            return response;
        } catch (Exception e) {
            log.error("하위 카테고리 목록 조회 실패: parentId={}, 오류={}", parentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 카테고리 이동 -> 부모 카테고리 변경
     */
    @Override
    public void moveCategory(Long id, Long newParentId) {
        try {
            log.info("카테고리 이동 시작: ID={}, newParentId={}", id, newParentId);
            
            // 자기 자신을 부모로 설정하는 것 방지
            if (id.equals(newParentId)) {
                throw new CircularReferenceException(id, newParentId);
            }

            Optional<BoardCategory> result = boardCategoryRepository.findById(id);
            BoardCategory category = result.orElseThrow(() ->
                    new BoardCategoryNotFoundException(id));

            // 새 부모 카테고리가 존재하는지 확인 (null이면 루트 카테고리로 이동)
            if (newParentId != null) {
                Optional<BoardCategory> parentResult = boardCategoryRepository.findById(newParentId);
                if (parentResult.isEmpty()) {
                    throw new BoardCategoryNotFoundException(newParentId);
                }

                // 순환 참조 방지 (새 부모가 현재 카테고리의 하위 카테고리인 경우)
                List<BoardCategory> descendants = boardCategoryRepository.findByParentId(id);
                if (descendants.stream().anyMatch(desc -> desc.getId().equals(newParentId))) {
                    throw new CircularReferenceException(id, newParentId);
                }
            }

            // 카테고리 이동
            BoardCategory updatedCategory = category.moveTo(newParentId, getCurrentUserId());
            boardCategoryRepository.save(updatedCategory);
            log.info("카테고리 이동 완료: ID={}, newParentId={}", id, newParentId);
        } catch (BoardCategoryNotFoundException | CircularReferenceException e) {
            log.error("카테고리 이동 실패: ID={}, newParentId={}, 오류={}", id, newParentId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("카테고리 이동 중 오류 발생: ID={}, newParentId={}, 오류={}", id, newParentId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 카테고리 활성화/비활성화
     */
    @Override
    public void setActive(Long id, boolean isActive) {
        try {
            log.info("카테고리 활성화 상태 변경 시작: ID={}, isActive={}", id, isActive);
            
            Optional<BoardCategory> result = boardCategoryRepository.findById(id);
            BoardCategory category = result.orElseThrow(() ->
                    new BoardCategoryNotFoundException(id));

            if (category.isActive() != isActive) {
                if (!isActive) {
                    // 비활성화
                    category = category.deactivate(getCurrentUserId());
                } else {
                    // 활성화
                    category = BoardCategory.builder()
                            .id(category.getId())
                            .categoryName(category.getCategoryName())
                            .description(category.getDescription())
                            .boardId(category.getBoardId())
                            .createdBy(category.getCreatedBy())
                            .updatedBy(getCurrentUserId())
                            .parentId(category.getParentId())
                            .isActive(true)
                            .sortOrder(category.getSortOrder())
                            .recommendThreshold(category.getRecommendThreshold())
                            .createdAt(category.getCreatedAt())
                            .updatedAt(java.time.LocalDateTime.now())
                            .children(category.getChildren())
                            .build();
                }

                boardCategoryRepository.save(category);
                log.info("카테고리 활성화 상태 변경 완료: ID={}, isActive={}", id, isActive);
            } else {
                log.info("카테고리 활성화 상태가 동일하여 변경 안함: ID={}, isActive={}", id, isActive);
            }
        } catch (BoardCategoryNotFoundException e) {
            log.error("카테고리를 찾을 수 없음: ID={}", id);
            throw e;
        } catch (Exception e) {
            log.error("카테고리 활성화 상태 변경 중 오류 발생: ID={}, isActive={}, 오류={}", id, isActive, e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 카테고리 추천게시글 상승 추천개수 변경
     */
    @Override
    public void changeRecommendThreshold(Long id, int threshold) {
        try {
            log.info("카테고리 추천 기준값 변경 시작: ID={}, threshold={}", id, threshold);
            
            if (threshold < 0) {
                throw new IllegalArgumentException("추천 기준값은 0 이상이어야 합니다: " + threshold);
            }
            
            Optional<BoardCategory> result = boardCategoryRepository.findById(id);
            BoardCategory category = result.orElseThrow(() ->
                    new BoardCategoryNotFoundException(id));

            if (category.getRecommendThreshold() != threshold) {
                BoardCategory updatedCategory = category.changeRecommendThreshold(threshold, getCurrentUserId());
                boardCategoryRepository.save(updatedCategory);
                log.info("카테고리 추천 기준값 변경 완료: ID={}, threshold={}", id, threshold);
            } else {
                log.info("카테고리 추천 기준값이 동일하여 변경 안함: ID={}, threshold={}", id, threshold);
            }
        } catch (BoardCategoryNotFoundException e) {
            log.error("카테고리를 찾을 수 없음: ID={}", id);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("잘못된 추천 기준값: ID={}, threshold={}, 오류={}", id, threshold, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("카테고리 추천 기준값 변경 중 오류 발생: ID={}, threshold={}, 오류={}", id, threshold, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 게시판별 카테고리 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<BoardCategoryResponseDTO> getListByBoardId(Long boardId) {
        try {
            log.info("게시판별 카테고리 목록 조회 시작: boardId={}", boardId);
            List<BoardCategory> categories = boardCategoryRepository.findByBoardId(boardId);
            List<BoardCategoryResponseDTO> response = categories.stream()
                    .map(boardCategoryMapper::domainToResponseDto)
                    .collect(Collectors.toList());
            log.info("게시판별 카테고리 목록 조회 완료: {} 개", response.size());
            return response;
        } catch (Exception e) {
            log.error("게시판별 카테고리 목록 조회 실패: boardId={}, 오류={}", boardId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 현재 인증된 사용자 ID를 가져옵니다.
     * TODO: 실제 인증 시스템 구현 후 Security Context에서 가져오도록 수정
     */
    private Long getCurrentUserId() {
        // 임시로 1L 반환 (실제로는 SecurityContext에서 가져와야 함)
        return 1L;
    }

}
