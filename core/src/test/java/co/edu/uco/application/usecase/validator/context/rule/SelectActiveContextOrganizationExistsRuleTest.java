package co.edu.uco.application.usecase.validator.context.rule;

import co.edu.uco.application.primaryports.dto.context.SelectActiveContextDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectActiveContextOrganizationExistsRuleTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final String NOT_FOUND_MESSAGE = "Organization not found";

    @Mock
    private CatalogPort catalogPort;
    @Mock
    private OrganizationRepository organizationRepository;

    private SelectActiveContextOrganizationExistsRule rule;

    @BeforeEach
    void setUp() {
        rule = new SelectActiveContextOrganizationExistsRule(catalogPort, organizationRepository);
    }

    @Test
    void validate_doesNotThrow_whenOrganizationExists() {
        when(organizationRepository.findById(ORGANIZATION_ID))
                .thenReturn(Optional.of(new OrganizationEntity()));
        SelectActiveContextDTO context = context(ORGANIZATION_ID.toString());

        assertThatCode(() -> rule.validate(context)).doesNotThrowAnyException();

        verify(organizationRepository).findById(ORGANIZATION_ID);
    }

    @Test
    void validate_throwsNotFoundUsingFun156_whenOrganizationDoesNotExist() {
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_156.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);
        SelectActiveContextDTO context = context(ORGANIZATION_ID.toString());

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(NotFoundException.class)
                .satisfies(exception -> assertThat((NotFoundException) exception)
                        .extracting(NotFoundException::getHttpStatus, NotFoundException::getUserMessage)
                        .containsExactly(404, NOT_FOUND_MESSAGE));
        verify(catalogPort).getMessage(MessageCatalogCodeEnum.FUN_156.getCode());
    }

    @ParameterizedTest(name = "organizationId=[{0}]")
    @ValueSource(strings = {"not-a-uuid", "", "00000000-0000-0000-0000-000000000000"})
    void validate_throwsNotFoundUsingFun156WithoutRepositoryLookup_whenOrganizationIdIsNotAUsableUuid(
            String organizationId) {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_156.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        SelectActiveContextDTO context = context(organizationId);

        assertThatThrownBy(() -> rule.validate(context))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(organizationRepository);
    }

    @Test
    void validate_throwsNotFoundWithoutRepositoryLookup_whenContextIsNull() {
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_156.getCode()))
                .thenReturn(NOT_FOUND_MESSAGE);

        assertThatThrownBy(() -> rule.validate(null)).isInstanceOf(NotFoundException.class);

        verifyNoInteractions(organizationRepository);
    }

    private static SelectActiveContextDTO context(String organizationId) {
        return SelectActiveContextDTO.builder().organizationId(organizationId).build();
    }
}
