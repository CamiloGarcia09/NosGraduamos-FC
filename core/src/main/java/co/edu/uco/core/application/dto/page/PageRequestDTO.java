package co.edu.uco.core.application.dto.page;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class PageRequestDTO {
    private String page;
    private String sort;
    private String columnSort;
    private String size;
} 