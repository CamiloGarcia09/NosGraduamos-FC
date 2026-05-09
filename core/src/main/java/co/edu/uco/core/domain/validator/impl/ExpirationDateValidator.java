package co.edu.uco.core.domain.validator.impl;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public final class ExpirationDateValidator implements Validator<LocalDateTime> {
    @Override
    public void validate(LocalDateTime expirationDate) {
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw BusinessRuleException.buildUserException(DetailMessageEnum.FUN_037.getContent());
        }
    }
}