package co.edu.uco.core.application.mapper;

import co.edu.uco.core.application.dto.page.PageRequestDTO;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.*;
import static co.edu.uco.utils.helper.UtilObject.isNullObject;
import static co.edu.uco.utils.helper.UtilText.getDefault;

@Component
public final class SimplePageMapper {
    public SimplePageRequest toSimplePageRequest(PageRequestDTO dto) {
        if (isNullObject(dto)) {
            return new SimplePageRequest();
        }
        var request = new SimplePageRequest();
        request.setPage(convertToInt(dto.getPage(), REQUEST_PAGE_DEFAULT));
        request.setSize(convertToInt(dto.getSize(), REQUEST_SIZE_DEFAULT));
        request.setSort(getDefault(dto.getSort(), REQUEST_PAGE_SORT_ASC));
        request.setColumnSort(getDefault(dto.getColumnSort(), REQUEST_COLUMN_SORT_DEFAULT));
        return request;
    }
    public PageRequestDTO toPageRequestDTO(SimplePageRequest request) {
        if (isNullObject(request)) {
            return new PageRequestDTO();
        }
        return PageRequestDTO.builder()
                .page(String.valueOf(request.getPage()))
                .size(String.valueOf(request.getSize()))
                .sort(request.getSort())
                .columnSort(request.getColumnSort())
                .build();
    }
    private int convertToInt(String value, int defaultValue) {
        if (isNullObject(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}