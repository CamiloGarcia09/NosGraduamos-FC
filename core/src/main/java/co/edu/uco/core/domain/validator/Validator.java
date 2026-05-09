package co.edu.uco.core.domain.validator;

import co.edu.uco.utils.exception.BusinessRuleException;

public interface Validator<D> {
    void validate(D data) throws BusinessRuleException;
}