package co.edu.uco.core.domain.validator.page;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.port.out.repository.SimplePageRequest;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.core.CrosswordsConstant.REQUEST_PAGE_DEFAULT;
import static co.edu.uco.utils.helper.UtilNumeric.isLessThan;

@Component
public final class PageNumberValidator implements Validator<SimplePageRequest> {
    @Override
    public void validate(SimplePageRequest data) throws BusinessRuleException {
        if (isLessThan(data.getPage(), REQUEST_PAGE_DEFAULT)) {
            throw BusinessRuleException.buildUserException(DetailMessageEnum.FUN_032.getContent());
        }
    }
}