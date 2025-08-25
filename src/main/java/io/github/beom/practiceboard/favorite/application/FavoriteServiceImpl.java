package io.github.beom.practiceboard.favorite.application;

import io.github.beom.practiceboard.favorite.domain.Favorite;
import io.github.beom.practiceboard.favorite.domain.FavoriteTargetType;
import io.github.beom.practiceboard.favorite.exception.FavoriteAlreadyExistsException;
import io.github.beom.practiceboard.favorite.exception.FavoriteNotFoundException;
import io.github.beom.practiceboard.favorite.presentation.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 좋아요 서비스 구현체
 * 좋아요 관련 비즈니스 로직을 처리합니다.
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    
    /**
     * 좋아요 추가
     * 이미 좋아요한 대상에 대해서는 예외를 발생시킵니다.
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입 (board, comment 등)
     * @param targetId 대상 ID
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     * @throws IllegalStateException 이미 좋아요한 대상인 경우
     */
    @Transactional
    public void addFavorite(Long userId, String targetType, Long targetId) {
        try {
            log.info("좋아요 추가 시작: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            validateTargetType(targetType);
            
            if (favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
                throw new FavoriteAlreadyExistsException(userId, targetType, targetId);
            }
            
            Favorite favorite = Favorite.create(userId, targetType, targetId);
            favoriteRepository.save(favorite);
            log.info("좋아요 추가 완료: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
        } catch (FavoriteAlreadyExistsException e) {
            log.error("좋아요 중복 추가 시도: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인수: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 추가 중 오류 발생: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 좋아요 제거
     * 좋아요하지 않은 대상에 대해서는 예외를 발생시킵니다.
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     * @throws IllegalStateException 좋아요하지 않은 대상인 경우
     */
    @Transactional
    public void removeFavorite(Long userId, String targetType, Long targetId) {
        try {
            log.info("좋아요 제거 시작: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            validateTargetType(targetType);
            
            Favorite favorite = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                    .orElseThrow(() -> new FavoriteNotFoundException(userId, targetType, targetId));
            
            favoriteRepository.delete(favorite);
            log.info("좋아요 제거 완료: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
        } catch (FavoriteNotFoundException e) {
            log.error("좋아요를 찾을 수 없음: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인수: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 제거 중 오류 발생: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 좋아요 토글 (좋아요/취소)
     * 좋아요가 되어있으면 취소하고, 안되어있으면 추가합니다.
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 추가되었으면 true, 취소되었으면 false
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     */
    @Transactional
    public boolean toggleFavorite(Long userId, String targetType, Long targetId) {
        try {
            log.info("좋아요 토글 시작: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            validateTargetType(targetType);
            
            boolean exists = favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
            
            if (exists) {
                // 좋아요 제거 (내부 메서드 호출로 try-catch 자동 처리)
                removeFavorite(userId, targetType, targetId);
                log.info("좋아요 토글 결과: 제거, userId={}, targetType={}, targetId={}", userId, targetType, targetId);
                return false;
            } else {
                // 좋아요 추가 (내부 메서드 호출로 try-catch 자동 처리)
                addFavorite(userId, targetType, targetId);
                log.info("좋아요 토글 결과: 추가, userId={}, targetType={}, targetId={}", userId, targetType, targetId);
                return true;
            }
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인수: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 좋아요 여부 확인
     * 특정 사용자가 특정 대상을 좋아요했는지 확인합니다.
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요했으면 true, 안했으면 false
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     */
    public boolean isFavorite(Long userId, String targetType, Long targetId) {
        try {
            log.debug("좋아요 여부 확인: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
            validateTargetType(targetType);
            boolean result = favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
            log.debug("좋아요 여부 결과: {}, userId={}, targetType={}, targetId={}", result, userId, targetType, targetId);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인수: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 여부 확인 중 오류 발생: userId={}, targetType={}, targetId={}, 오류={}", userId, targetType, targetId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 좋아요 수 조회
     * 특정 대상의 총 좋아요 수를 조회합니다.
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 수
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     */
    public long getFavoriteCount(String targetType, Long targetId) {
        try {
            log.debug("좋아요 수 조회: targetType={}, targetId={}", targetType, targetId);
            validateTargetType(targetType);
            long count = favoriteRepository.countByTargetTypeAndTargetId(targetType, targetId);
            log.debug("좋아요 수 결과: {}, targetType={}, targetId={}", count, targetType, targetId);
            return count;
        } catch (IllegalArgumentException e) {
            log.error("잘못된 인수: targetType={}, targetId={}, 오류={}", targetType, targetId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("좋아요 수 조회 중 오류 발생: targetType={}, targetId={}, 오류={}", targetType, targetId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 사용자의 좋아요 목록 조회
     * 특정 사용자가 특정 타입에 대해 좋아요한 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param targetType 대상 타입
     * @return 좋아요 목록
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     */
    public List<Favorite> getUserFavorites(Long userId, String targetType) {
        validateTargetType(targetType);
        return favoriteRepository.findByUserIdAndTargetType(userId, targetType);
    }
    
    /**
     * 특정 대상의 좋아요 목록 조회
     * 특정 대상을 좋아요한 모든 사용자 정보를 조회합니다.
     * 
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @return 좋아요 목록
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     */
    public List<Favorite> getTargetFavorites(String targetType, Long targetId) {
        validateTargetType(targetType);
        return favoriteRepository.findByTargetTypeAndTargetId(targetType, targetId);
    }
    
    /**
     * 대상 타입 유효성 검증
     * 
     * @param targetType 검증할 대상 타입
     * @throws IllegalArgumentException 유효하지 않은 대상 타입인 경우
     */
    private void validateTargetType(String targetType) {
        if (!FavoriteTargetType.isValid(targetType)) {
            throw new IllegalArgumentException("유효하지 않은 대상 타입입니다: " + targetType);
        }
    }
}