package co.edu.uco.application.usecase.validator.context;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;

public interface SelectActiveContextIdentifiersRule {

    void validate(SelectActiveContextDTO context);
}
