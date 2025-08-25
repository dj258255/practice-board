package io.github.beom.practiceboard.board.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryRequestDTO {
    private Long id;
    private String categoryName;
    private String description;
    private Long boardId;
    private Long parentId;
    private boolean isActive;
    private int sortOrder;
    private int recommendThreshold;
}
