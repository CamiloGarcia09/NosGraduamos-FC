package co.edu.uco.core.domain.port.out.repository;

import lombok.Data;

import static co.edu.uco.core.CrosswordsConstant.*;

@Data
public final class SimplePageRequest {
    private int page = REQUEST_PAGE_DEFAULT;
    private String sort = REQUEST_PAGE_SORT_ASC;
    private String columnSort = REQUEST_COLUMN_SORT_DEFAULT;
    private int size = REQUEST_SIZE_DEFAULT;
}