package co.edu.uco.core.domain.validator.impl;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.core.domain.validator.Validator;
import co.edu.uco.utils.exception.BusinessRuleException;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public final class ExpirationDateValidator implements Validator<LocalDateTime> {
    private final InMemoryCatalog inMemoryCatalog;
    public ExpirationDateValidator(InMemoryCatalog inMemoryCatalog) {
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(LocalDateTime expirationDate) {
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw BusinessRuleException.buildUserException(inMemoryCatalog.getContent(MessageKeyEnum.FUN_037.getKey()));
        }
    }
}