package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import co.edu.uco.utils.helper.UtilNumeric;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_DEFAULT;

@Component
public final class PageSizeValidator implements Validator<SimplePageRequest> {
    private static final int MAX_PAGE_SIZE = 100;
    private final InMemoryCatalog inMemoryCatalog;
    public PageSizeValidator(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (!UtilNumeric.isBetweenIncludingRanges(data.getSize(), REQUEST_PAGE_DEFAULT, MAX_PAGE_SIZE)) {
            throw BusinessRuleException.buildUserException(
                String.format(inMemoryCatalog.getContent(MessageKeyEnum.FUN_029.getKey()), MAX_PAGE_SIZE));
        }
    }
}