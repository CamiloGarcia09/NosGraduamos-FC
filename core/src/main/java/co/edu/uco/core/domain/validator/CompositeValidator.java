package co.edu.uco.core.domain.validator;

import co.edu.uco.core.application.catalog.strategy.inmemory.InMemoryCatalog;
import co.edu.uco.core.application.catalog.strategy.inmemory.enums.MessageKeyEnum;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static co.edu.uco.utils.helper.UtilObject.isNullObject;

@Component
public class CompositeValidator<T> implements Validator<T> {
    private final List<Validator<T>> validators;
    protected final InMemoryCatalog inMemoryCatalog;
    @Autowired
    public CompositeValidator(List<Validator<T>> validators, InMemoryCatalog inMemoryCatalog) {
        this.validators = validators;
        this.inMemoryCatalog = inMemoryCatalog;
    }
    @Override
    public void validate(T data) {
        if (validators.isEmpty()) {
            throw CrossWordsException.build(inMemoryCatalog.getContent(MessageKeyEnum.FUN_034.getKey()));
        }
        if (isNullObject(data)) {
            throw CrossWordsException.build(inMemoryCatalog.getContent(MessageKeyEnum.FUN_010.getKey()));
        }
        validators.forEach(validator -> validator.validate(data));
    }
}