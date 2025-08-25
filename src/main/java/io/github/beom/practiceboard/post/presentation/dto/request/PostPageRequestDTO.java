package io.github.beom.practiceboard.post.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 게시글 페이징 요청 DTO
 * 페이징 및 검색 조건을 포함하는 요청 데이터 객체
 */
@Schema(description = "게시글 페이징 요청 정보")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPageRequestDTO {
    
    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    @Builder.Default
    private int page = 1;
    
    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Builder.Default
    private int size = 10;
    
    @Schema(description = "검색 유형 (t: 제목, c: 내용, a: 작성자, tc: 제목+내용, ta: 제목+작성자, tca: 제목+작성자+내용)", example = "tc")
    private String type; // 검색의 종류 t(title), c(content), a(author), tc, ta, tca
    
    @Schema(description = "검색 키워드", example = "검색어")
    private String keyword; // 검색 키워드
    
    @Schema(description = "페이징 링크 생성용", hidden = true)
    private String link; // 페이징 링크 생성용
    
    // 정렬 옵션
    @Schema(description = "정렬 기준 (id, createdAt, viewCount, likeCount 등)", example = "createdAt")
    @Builder.Default
    private String sort = "createdAt"; // 기본 정렬: 생성일시
    
    @Schema(description = "정렬 방향 (asc, desc)", example = "desc")
    @Builder.Default
    private String direction = "desc"; // 기본 방향: 내림차순
    
    // 필터 옵션
    @Schema(description = "특정 게시판 ID로 필터링", example = "1")
    private Long boardId; // 특정 게시판
    
    @Schema(description = "특정 카테고리 ID로 필터링", example = "1")
    private Long categoryId; // 특정 카테고리
    
    @Schema(description = "특정 작성자 ID로 필터링", example = "1")
    private Long authorId; // 특정 작성자
    
    @Schema(description = "게시글 타입 (NORMAL: 일반, NOTICE: 공지사항, FEATURED: 추천게시글)", example = "NORMAL")
    private String postType; // 게시글 타입
    
    @Schema(description = "고정글만 조회 여부", example = "false")
    private Boolean pinnedOnly; // 고정글만 조회
    
    @Schema(description = "추천글만 조회 여부", example = "false")
    private Boolean featuredOnly; // 추천글만 조회
    
    /**
     * 검색 유형을 배열로 변환
     * @return 검색 유형 배열 (null이면 검색하지 않음)
     */
    @Schema(hidden = true)
    public String[] getTypes(){
        if(type == null || type.isEmpty()){
            return null;
        }
        return type.split("");
    }
    
    /**
     * 페이징 정보 생성
     * @param props 정렬 기준 필드 (기본값 사용시 생략 가능)
     * @return Pageable 객체
     */
    @Schema(hidden = true)
    public Pageable getPageable(String...props) {
        int validPage = Math.max(1, this.page);
        int validSize = Math.max(1, Math.min(100, this.size));
        
        // props가 제공되지 않으면 기본 정렬 사용
        if (props.length == 0) {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            return PageRequest.of(validPage-1, validSize, Sort.by(sortDirection, sort));
        }
        
        return PageRequest.of(validPage-1, validSize, Sort.by(props).descending());
    }
    
    /**
     * 0부터 시작하는 페이지 번호 반환 (JPA용)
     */
    @Schema(hidden = true)
    public int getSkip() {
        return (Math.max(1, page) - 1) * Math.max(1, size);
    }
    
    /**
     * 검색 조건이 있는지 확인
     */
    @Schema(hidden = true)
    public boolean hasSearchCondition() {
        return keyword != null && !keyword.trim().isEmpty() && 
               type != null && !type.trim().isEmpty();
    }
    
    /**
     * 필터 조건이 있는지 확인
     */
    @Schema(hidden = true)
    public boolean hasFilterCondition() {
        return boardId != null || categoryId != null || authorId != null || 
               postType != null || Boolean.TRUE.equals(pinnedOnly) || Boolean.TRUE.equals(featuredOnly);
    }
    
    /**
     * 페이징 링크 생성을 위한 파라미터들
     */
    @Schema(hidden = true)
    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=").append(this.page);
            builder.append("&size=").append(this.size);
            
            if (hasSearchCondition()) {
                builder.append("&type=").append(this.type);
                builder.append("&keyword=").append(this.keyword);
            }
            
            if (boardId != null) {
                builder.append("&boardId=").append(this.boardId);
            }
            
            if (categoryId != null) {
                builder.append("&categoryId=").append(this.categoryId);
            }
            
            if (authorId != null) {
                builder.append("&authorId=").append(this.authorId);
            }
            
            if (postType != null) {
                builder.append("&postType=").append(this.postType);
            }
            
            if (Boolean.TRUE.equals(pinnedOnly)) {
                builder.append("&pinnedOnly=true");
            }
            
            if (Boolean.TRUE.equals(featuredOnly)) {
                builder.append("&featuredOnly=true");
            }
            
            builder.append("&sort=").append(this.sort);
            builder.append("&direction=").append(this.direction);
            
            this.link = builder.toString();
        }
        return link;
    }
    
    // Setter에서도 유효성 검증
    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(100, size));
    }
}