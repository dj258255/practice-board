package io.github.beom.practiceboard.comment.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 댓글 페이지 요청 DTO
 * 댓글 페이징 및 검색 조건을 포함하는 요청 데이터 객체
 */
@Schema(description = "댓글 페이지 요청 정보")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageRequestDTO {

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    @Builder.Default
    private int page = 1;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Builder.Default
    private int size = 10;

    @Schema(description = "게시글 ID (필수: 특정 게시글의 댓글만 조회)", example = "1")
    private Long boardId;

    @Schema(description = "검색 유형 (c: 내용, w: 작성자, cw: 내용+작성자)", example = "c")
    private String type; // 검색의 종류 c(content), w(writer), cw(content+writer)

    @Schema(description = "검색 키워드", example = "댓글")
    private String keyword;

    @Schema(description = "부모 댓글 ID (특정 댓글의 대댓글만 조회)", example = "1")
    private Long parentId;

    @Schema(description = "댓글 깊이 (0: 최상위 댓글만, 1: 대댓글만)", example = "0")
    private Integer depth;

    /**
     * 검색 유형을 배열로 변환
     * @return 검색 유형 배열 (null이면 검색하지 않음)
     */
    @Schema(hidden = true)
    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    /**
     * 페이징 정보 생성
     * @param props 정렬 기준 필드 (기본: )
     * @return Pageable 객체
     */
    @Schema(hidden = true)
    public Pageable getPageable(String... props) {
        int validPage = Math.max(1, this.page);
        int validSize = Math.max(1, Math.min(100, this.size));
        
        // 정렬 기준이 없으면 등록일 기준으로 오름차순 정렬 (댓글은 시간순이 자연스러움)
        Sort sort = props.length > 0 ? 
            Sort.by(props).ascending() : 
            Sort.by("").ascending();
        
        return PageRequest.of(validPage - 1, validSize, sort);
    }

    /**
     * 최상위 댓글만 조회하는지 확인
     * @return 최상위 댓글만 조회 여부
     */
    @Schema(hidden = true)
    public boolean isRootCommentsOnly() {
        return depth != null && depth == 0;
    }

    /**
     * 대댓글만 조회하는지 확인
     * @return 대댓글만 조회 여부
     */
    @Schema(hidden = true)
    public boolean isChildCommentsOnly() {
        return depth != null && depth == 1;
    }

    /**
     * 특정 부모 댓글의 대댓글 조회인지 확인
     * @return 특정 부모 댓글의 대댓글 조회 여부
     */
    @Schema(hidden = true)
    public boolean isParentSpecificSearch() {
        return parentId != null;
    }

    // Setter에서도 유효성 검증
    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(100, size));
    }

    public void setDepth(Integer depth) {
        if (depth != null) {
            this.depth = Math.max(0, Math.min(1, depth)); // 0 또는 1만 허용
        } else {
            this.depth = null;
        }
    }
}