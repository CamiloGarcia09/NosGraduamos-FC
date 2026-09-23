package co.edu.uco.application.usecase.validator.rule;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.application.usecase.validator.specification.Specification;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;

/**
 * Base común para reglas de negocio individuales. Cada regla encapsula una
 * {@link Specification} reutilizable y el código de catálogo con el que se
 * resuelve el mensaje que recibe el usuario cuando la regla no se cumple.
 *
 * @param <T> tipo de dato sobre el que se aplica la regla
 */
public abstract class RuleValidator<T> implements Validator<T> {

    private final CatalogPort catalogPort;
    private final Specification<T> rule;
    private final MessageCatalogCodeEnum catalogCode;

    protected RuleValidator(CatalogPort catalogPort, Specification<T> rule, MessageCatalogCodeEnum catalogCode) {
        this.catalogPort = catalogPort;
        this.rule = rule;
        this.catalogCode = catalogCode;
    }

    @Override
    public final void validate(T data) throws BusinessRuleException {
        if (!rule.isSatisfiedBy(data)) {
            throw BusinessRuleException.buildUserException(catalogPort.getMessage(catalogCode.getCode()));
        }
    }
}