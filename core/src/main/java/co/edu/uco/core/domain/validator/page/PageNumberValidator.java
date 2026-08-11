package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_DEFAULT;
import static co.edu.uco.utils.helper.UtilNumeric.isLessThan;

@Component
public final class PageNumberValidator implements Validator<SimplePageRequest> {
    private final InMemoryCatalog inMemoryCatalog;
    public PageNumberValidator(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (isLessThan(data.getPage(), REQUEST_PAGE_DEFAULT)) {
            throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.FUN_032.getKey()));
        }
    }
}