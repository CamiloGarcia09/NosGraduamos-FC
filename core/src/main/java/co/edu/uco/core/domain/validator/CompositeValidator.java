package co.edu.uco.core.domain.validator;

import co.edu.uco.core.domain.port.out.catalog.CatalogPort;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static co.edu.uco.utils.helper.UtilObject.isNullObject;

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
            throw CrossWordsException.build(catalogPort.getMessage("FUN_034"));
        }
        if (isNullObject(data)) {
            throw CrossWordsException.build(catalogPort.getMessage("FUN_010"));
        }
        validators.forEach(validator -> validator.validate(data));
    }
}