package co.edu.uco.application.usecase.validator.impl;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.LocalDateTime;

import static co.edu.uco.crosscutting.helpers.UtilDate.nowUtc;

@Component
public final class ExpirationDateValidator implements Validator<LocalDateTime> {
    private final CatalogPort catalogPort;
    private final Clock clock;
    public ExpirationDateValidator(CatalogPort catalogPort, Clock clock) {
        this.catalogPort = catalogPort;
        this.clock = clock;
    }
    @Override
    public void validate(LocalDateTime expirationDate) {
        if (expirationDate.isBefore(nowUtc(clock))) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_037.getCode()));
        }
    }
}
