package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.Validator;
import co.edu.uco.application.usecase.validator.message.rule.MessageApplicationIdRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageCodeRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageContentMaxLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageContentMinLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageContentRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageEnvironmentIdRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageFunctionalityIdRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageTitleMaxLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageTitleMinLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageTitleRequiredRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class CreateMessageCompositeValidator extends CompositeValidator<CreateMessageDTO> {

    public CreateMessageCompositeValidator(CatalogPort catalogPort) {
        super(List.of(
                new MessageCodeRequiredRule(catalogPort),
                new MessageTitleRequiredRule(catalogPort),
                new MessageTitleMinLengthRule(catalogPort),
                new MessageTitleMaxLengthRule(catalogPort),
                new MessageContentRequiredRule(catalogPort),
                new MessageContentMinLengthRule(catalogPort),
                new MessageContentMaxLengthRule(catalogPort),
                new MessageApplicationIdRequiredRule(catalogPort),
                new MessageEnvironmentIdRequiredRule(catalogPort),
                new MessageFunctionalityIdRequiredRule(catalogPort)
        ), catalogPort);
    }
}