package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.usecase.validator.CompositeValidator;
import co.edu.uco.application.usecase.validator.message.rule.MessageApplicationIdRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageCatalogIdExistsRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageCatalogIdRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageCatalogIdUuidRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageCodeRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageContentMaxLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageContentMinLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageContentRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageFunctionalityIdRequiredRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageTitleMaxLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageTitleMinLengthRule;
import co.edu.uco.application.usecase.validator.message.rule.MessageTitleRequiredRule;
import org.springframework.stereotype.Component;

import java.util.List;

import static co.edu.uco.application.secondaryports.repository.ReferenceCatalog.MESSAGE_CATEGORY;
import static co.edu.uco.application.secondaryports.repository.ReferenceCatalog.MESSAGE_ENVIRONMENT_STATE;
import static co.edu.uco.application.secondaryports.repository.ReferenceCatalog.MESSAGE_STATE;
import static co.edu.uco.application.secondaryports.repository.ReferenceCatalog.MESSAGE_TYPE;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_190;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_191;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_192;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_193;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_194;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_195;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_196;
import static co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum.FUN_197;

@Component
public final class CreateMessageCompositeValidator extends CompositeValidator<CreateMessageDTO> {

    private final CreateMessageContextRule contextRule;

    public CreateMessageCompositeValidator(CatalogPort catalogPort,
                                           CreateMessageContextRule contextRule,
                                           RecordExistsCatalogPort recordExistsCatalogPort) {
        super(List.of(
                new MessageCodeRequiredRule(catalogPort),
                new MessageTitleRequiredRule(catalogPort),
                new MessageTitleMinLengthRule(catalogPort),
                new MessageTitleMaxLengthRule(catalogPort),
                new MessageContentRequiredRule(catalogPort),
                new MessageContentMinLengthRule(catalogPort),
                new MessageContentMaxLengthRule(catalogPort),
                new MessageApplicationIdRequiredRule(catalogPort),
                new MessageFunctionalityIdRequiredRule(catalogPort),
                new MessageCatalogIdRequiredRule(catalogPort, CreateMessageDTO::getTypeId, FUN_190),
                new MessageCatalogIdUuidRule(catalogPort, CreateMessageDTO::getTypeId),
                new MessageCatalogIdExistsRule(catalogPort, recordExistsCatalogPort,
                        CreateMessageDTO::getTypeId, MESSAGE_TYPE, FUN_191),
                new MessageCatalogIdRequiredRule(catalogPort, CreateMessageDTO::getCategoryId, FUN_192),
                new MessageCatalogIdUuidRule(catalogPort, CreateMessageDTO::getCategoryId),
                new MessageCatalogIdExistsRule(catalogPort, recordExistsCatalogPort,
                        CreateMessageDTO::getCategoryId, MESSAGE_CATEGORY, FUN_193),
                new MessageCatalogIdRequiredRule(catalogPort, CreateMessageDTO::getStatusId, FUN_194),
                new MessageCatalogIdUuidRule(catalogPort, CreateMessageDTO::getStatusId),
                new MessageCatalogIdExistsRule(catalogPort, recordExistsCatalogPort,
                        CreateMessageDTO::getStatusId, MESSAGE_STATE, FUN_195),
                new MessageCatalogIdRequiredRule(catalogPort,
                        CreateMessageDTO::getMessageEnvironmentStateId, FUN_196),
                new MessageCatalogIdUuidRule(catalogPort, CreateMessageDTO::getMessageEnvironmentStateId),
                new MessageCatalogIdExistsRule(catalogPort, recordExistsCatalogPort,
                        CreateMessageDTO::getMessageEnvironmentStateId, MESSAGE_ENVIRONMENT_STATE, FUN_197)
        ), catalogPort);
        this.contextRule = contextRule;
    }

    public void validate(CreateMessageDTO dto, String authenticatedEnvironmentId) {
        super.validate(dto);
        contextRule.validate(dto, authenticatedEnvironmentId);
    }
}
