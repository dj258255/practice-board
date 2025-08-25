package io.github.beom.practiceboard.post.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 게시글 페이징 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPageResponseDTO<E> {
    
    private int page;
    private int size;
    private int totalPages;
    private long totalCount;
    
    // 페이징 관련
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // 데이터
    private List<E> content;
    
    // 검색 조건 (재검색을 위해)
    private String type;
    private String keyword;
    
    // 페이징 버튼용 페이지 번호 목록
    private List<Integer> pageList;
    
    // 이전/다음 페이지 번호
    private int prevPage;
    private int nextPage;
    
    public static <T> PostPageResponseDTO<T> of(List<T> content, int page, int size, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        
        return PostPageResponseDTO.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalCount(totalCount)
                .first(page == 1)
                .last(page == totalPages)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .prevPage(Math.max(1, page - 1))
                .nextPage(Math.min(totalPages, page + 1))
                .build();
    }
    
    /**
     * 페이징 버튼용 페이지 번호 목록 생성
     */
    public List<Integer> getPageList() {
        if (pageList == null) {
            int start = Math.max(1, page - 2);
            int end = Math.min(totalPages, page + 2);
            
            pageList = java.util.stream.IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(java.util.stream.Collectors.toList());
        }
        return pageList;
    }
}