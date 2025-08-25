package io.github.beom.practiceboard.board.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryResponseDTO {
    private Long id;
    private String categoryName;
    private String description;
    private Long boardId;
    private Long parentId;
    private boolean isActive;
    private int sortOrder;
    private int recommendThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BoardCategoryResponseDTO> children;
}
