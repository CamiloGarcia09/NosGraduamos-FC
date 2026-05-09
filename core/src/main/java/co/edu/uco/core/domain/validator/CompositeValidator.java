package co.edu.uco.core.domain.validator;

import co.edu.uco.core.application.catalog.strategy.inmemory.enums.DetailMessageEnum;
import co.edu.uco.utils.exception.CrossWordsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static co.edu.uco.utils.helper.UtilObject.isNullObject;

@Component
public class CompositeValidator<T> implements Validator<T> {
    private final List<Validator<T>> validators;
    @Autowired
    public CompositeValidator(List<Validator<T>> validators) {
        this.validators = validators;
    }
    @Override
    public void validate(T data) {
        if (validators.isEmpty()) {
            throw CrossWordsException.build(DetailMessageEnum.FUN_034.getContent());
        }
        if (isNullObject(data)) {
            throw CrossWordsException.build(DetailMessageEnum.FUN_010.getContent());
        }
        validators.forEach(validator -> validator.validate(data));
    }
}