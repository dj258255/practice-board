package io.github.beom.practiceboard.board.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 게시판 페이징 요청 DTO
 * 게시판 목록 조회시 사용되는 페이징 및 검색 조건
 */
@Schema(description = "게시판 페이징 요청 정보")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardPageRequestDTO {

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    @Builder.Default
    private int page = 1;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Builder.Default
    private int size = 10;

    @Schema(description = "검색 유형 (n: 게시판명, d: 설명, s: 슬러그, nd: 게시판명+설명)", example = "nd")
    private String type; // 검색의 종류 n(name), d(description), s(slug), nd, ns, nds

    @Schema(description = "검색 키워드", example = "자유게시판")
    private String keyword;

    @Schema(description = "게시판 타입 (NORMAL: 일반, NOTICE: 공지사항, QNA: Q&A)", example = "NORMAL")
    private String boardType;

    @Schema(description = "게시판 상태 (ACTIVE: 활성, INACTIVE: 비활성)", example = "ACTIVE")
    private String status;

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
     * @param props 정렬 기준 필드
     * @return Pageable 객체
     */
    @Schema(hidden = true)
    public Pageable getPageable(String...props) {
        int validPage = Math.max(1, this.page);
        int validSize = Math.max(1 , Math.min(100, this.size));
        return PageRequest.of(validPage-1, validSize, Sort.by(props).descending());
    }

    /**
     * 검색 조건이 있는지 확인
     */
    @Schema(hidden = true)
    public boolean hasSearchCondition() {
        return keyword != null && !keyword.trim().isEmpty() && 
               type != null && !type.trim().isEmpty();
    }

    // Setter에서도 유효성 검증
    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(100, size));
    }
}