package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.RecordExistsCatalogPort;
import co.edu.uco.application.secondaryports.repository.ReferenceCatalog;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import static co.edu.uco.crosscutting.helpers.UtilObject.isNullObject;
import static co.edu.uco.crosscutting.helpers.UtilText.isEmptyOrNull;

@Component
public final class CreateMessageCompositeValidator {

    private final CatalogPort catalogPort;
    private final CreateMessageContextRule contextRule;
    private final RecordExistsCatalogPort recordExistsCatalogPort;
    private final UUIDValidator uuidValidator;

    public CreateMessageCompositeValidator(CatalogPort catalogPort, CreateMessageContextRule contextRule,
                                           RecordExistsCatalogPort recordExistsCatalogPort,
                                           UUIDValidator uuidValidator) {
        this.catalogPort = catalogPort;
        this.contextRule = contextRule;
        this.recordExistsCatalogPort = recordExistsCatalogPort;
        this.uuidValidator = uuidValidator;
    }

    public void validate(CreateMessageDTO dto, String authenticatedEnvironmentId) {
        if (isNullObject(dto)) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_010.getCode())
            );
        }

        if (isEmptyOrNull(dto.getCode())) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_040.getCode())
            );
        }

        if (isEmptyOrNull(dto.getTitle())) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_022.getCode())
            );
        }

        if (dto.getTitle().trim().length() < 10) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_020.getCode())
            );
        }

        if (dto.getTitle().trim().length() > 50) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_021.getCode())
            );
        }

        if (isEmptyOrNull(dto.getContent())) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_017.getCode())
            );
        }

        if (dto.getContent().trim().length() < 10) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_018.getCode())
            );
        }

        if (dto.getContent().trim().length() > 100) {
            throw BusinessRuleException.buildUserException(
                    catalogPort.getMessage(MessageCatalogCodeEnum.FUN_019.getCode())
            );
        }

        if (isEmptyOrNull(dto.getApplicationId())) {
            throw BusinessRuleException.buildUserException("El id de la aplicación es requerido.");
        }

        if (isEmptyOrNull(dto.getFunctionalityId())) {
            throw BusinessRuleException.buildUserException("El id de la funcionalidad es requerido.");
        }

        validateCatalogId(dto.getTypeId(), ReferenceCatalog.MESSAGE_TYPE, "El tipo de mensaje");
        validateCatalogId(dto.getCategoryId(), ReferenceCatalog.MESSAGE_CATEGORY, "La categoría del mensaje");
        validateCatalogId(dto.getStatusId(), ReferenceCatalog.MESSAGE_STATE, "El estado del mensaje");
        validateCatalogId(dto.getMessageEnvironmentStateId(), ReferenceCatalog.MESSAGE_ENVIRONMENT_STATE,
                "El estado del mensaje en el ambiente");

        contextRule.validate(dto, authenticatedEnvironmentId);
    }

    private void validateCatalogId(String id, ReferenceCatalog catalog, String fieldName) {
        if (isEmptyOrNull(id)) {
            throw BusinessRuleException.buildUserException(fieldName + " es requerido.");
        }
        uuidValidator.validate(id);
        if (!recordExistsCatalogPort.exists(catalog, id)) {
            throw BusinessRuleException.buildUserException(fieldName + " no existe.");
        }
    }
}
