package co.edu.uco.application.usecase.validator.application;

import co.edu.uco.application.primaryports.dto.application.CreateApplicationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.application.usecase.validator.impl.UUIDValidator;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateApplicationOrganizationExistsRuleImplTest {

    private static final String REQUIRED_MESSAGE = "La organización es requerida.";
    private static final String NOT_FOUND_MESSAGE = "La organización no existe.";

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private CatalogPort catalogPort;

    private UUIDValidator uuidValidator;
    private CreateApplicationOrganizationExistsRuleImpl rule;

    @BeforeEach
    void setUp() {
        uuidValidator = new UUIDValidator(catalogPort);
        rule = new CreateApplicationOrganizationExistsRuleImpl(
                organizationRepository, uuidValidator, catalogPort);
    }

    @Test
    void validate_acceptsExistingOrganization() {
        UUID organizationId = UUID.randomUUID();
        when(organizationRepository.findById(organizationId))
                .thenReturn(Optional.of(new OrganizationEntity()));

        assertThatCode(() -> rule.validate(dtoWithOrganization(organizationId.toString())))
                .doesNotThrowAnyException();

        verify(organizationRepository).findById(organizationId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void validate_rejectsMissingOrganizationAndShortCircuits(String organizationId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_150.getCode())).thenReturn(REQUIRED_MESSAGE);

        assertThatThrownBy(() -> rule.validate(dtoWithOrganization(organizationId)))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo(REQUIRED_MESSAGE);

        verify(catalogPort).getMessage("FUN_150");
        verifyNoInteractions(organizationRepository);
    }

    @Test
    void validate_rejectsInvalidUuidBeforeRepositoryLookup() {
        assertThatThrownBy(() -> rule.validate(dtoWithOrganization("not-a-uuid")))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo("The UUID to be converted has no valid format.");

        verifyNoInteractions(organizationRepository);
        verify(catalogPort, never()).getMessage(MessageCatalogCodeEnum.FUN_151.getCode());
    }

    @Test
    void validate_rejectsUnknownOrganization() {
        UUID organizationId = UUID.randomUUID();
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_151.getCode())).thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(dtoWithOrganization(organizationId.toString())))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo(NOT_FOUND_MESSAGE);

        verify(organizationRepository).findById(organizationId);
        verify(catalogPort).getMessage("FUN_151");
    }

    private CreateApplicationDTO dtoWithOrganization(String organizationId) {
        return CreateApplicationDTO.builder().organizationId(organizationId).build();
    }
}
