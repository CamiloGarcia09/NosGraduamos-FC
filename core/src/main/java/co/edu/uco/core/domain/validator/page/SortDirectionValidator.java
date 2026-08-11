package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_SORT_ASC;
import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_SORT_DESC;

@Component
public final class SortDirectionValidator implements Validator<SimplePageRequest> {
    private final InMemoryCatalog inMemoryCatalog;
    public SortDirectionValidator(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (!data.getSort().equalsIgnoreCase(REQUEST_PAGE_SORT_ASC) && !data.getSort().equalsIgnoreCase(REQUEST_PAGE_SORT_DESC)) {
            throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.FUN_031.getKey()));
        }
    }
}