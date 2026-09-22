package co.edu.uco.application.usecase.validator.organization;

import co.edu.uco.application.primaryports.dto.organization.CreateOrganizationDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.repository.OrganizationRepository;
import co.edu.uco.application.usecase.domain.aggregate.entities.OrganizationEntity;
import co.edu.uco.crosscutting.catalog.MessageCatalogCodeEnum;
import co.edu.uco.crosscutting.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrganizationUniqueNameRuleImplTest {

    private static final String NAME = "UCO";
    private static final String DUPLICATE_MESSAGE = "Ya existe una organización con el nombre proporcionado.";

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private CatalogPort catalogPort;

    private CreateOrganizationUniqueNameRuleImpl rule;

    @BeforeEach
    void setUp() {
        rule = new CreateOrganizationUniqueNameRuleImpl(organizationRepository, catalogPort);
    }

    @Test
    void validate_acceptsNameWhenRepositoryReturnsEmpty() {
        when(organizationRepository.findByName(NAME)).thenReturn(Optional.empty());
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name(NAME).build();

        assertThatCode(() -> rule.validate(dto)).doesNotThrowAnyException();

        verify(organizationRepository).findByName(NAME);
    }

    @Test
    void validate_acceptsFiftyCharacterNameWhenRepositoryReturnsEmpty() {
        String boundaryName = "a".repeat(50);
        when(organizationRepository.findByName(boundaryName)).thenReturn(Optional.empty());
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name(boundaryName).build();

        assertThatCode(() -> rule.validate(dto)).doesNotThrowAnyException();

        verify(organizationRepository).findByName(boundaryName);
    }

    @Test
    void validate_queriesNormalizedNameFromDto() {
        CreateOrganizationDTO dto = new CreateOrganizationDTO();
        dto.setName("  UCO  ");
        when(organizationRepository.findByName(NAME)).thenReturn(Optional.empty());

        rule.validate(dto);

        verify(organizationRepository).findByName(NAME);
    }

    @Test
    void validate_rejectsNameWhenRepositoryReturnsOrganization() {
        when(organizationRepository.findByName(NAME)).thenReturn(Optional.of(new OrganizationEntity()));
        when(catalogPort.getMessage(MessageCatalogCodeEnum.FUN_149.getCode())).thenReturn(DUPLICATE_MESSAGE);
        CreateOrganizationDTO dto = CreateOrganizationDTO.builder().name(NAME).build();

        assertThatThrownBy(() -> rule.validate(dto))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("userMessage")
                .isEqualTo(DUPLICATE_MESSAGE);
        verify(catalogPort).getMessage("FUN_149");
    }
}
