package co.edu.uco.core.domain.port.out.repository.token;

import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static co.edu.uco.utils.helper.UtilPagination.toZeroBasedPage;

public final class PageBuilder {
    private PageBuilder() {}
    public static PageRequest createPageRequest(SimplePageRequest pageRequest) {
        int zeroBasedPage = toZeroBasedPage(pageRequest.getPage());
        Sort sort = Sort.by(
                Sort.Direction.fromString(pageRequest.getSort()),
                pageRequest.getColumnSort()
        );
        return PageRequest.of(zeroBasedPage, pageRequest.getSize(), sort);
    }
}
