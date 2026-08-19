package co.edu.uco.application.usecase.validator;

import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.CrossWordsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;

@Component
public class CompositeValidator<T> implements Validator<T> {

    private final List<Validator<T>> validators;
    protected final CatalogPort catalogPort;

    @Autowired
    public CompositeValidator(List<Validator<T>> validators, CatalogPort catalogPort) {
        this.validators = validators;
        this.catalogPort = catalogPort;
    }

    @Override
    public void validate(T data) {
        if (validators.isEmpty()) {
            throw CrossWordsException.build(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_034.getCode()));
        }
        if (isNullObject(data)) {
            throw CrossWordsException.build(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode()));
        }
        validators.forEach(validator -> validator.validate(data));
    }
}