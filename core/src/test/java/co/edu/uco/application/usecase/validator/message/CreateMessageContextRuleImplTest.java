package co.edu.uco.application.usecase.validator.message;

import co.edu.uco.application.primaryports.dto.message.CreateMessageDTO;
import co.edu.uco.application.secondaryports.catalog.CatalogPort;
import co.edu.uco.application.secondaryports.entity.ApplicationData;
import co.edu.uco.application.secondaryports.entity.EnvironmentData;
import co.edu.uco.application.secondaryports.entity.FunctionalityData;
import co.edu.uco.application.secondaryports.repository.EnvironmentRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.crosscutting.exceptions.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMessageContextRuleImplTest {

    private static final String APPLICATION_ID = "123e4567-e89b-12d3-a456-426614175000";
    private static final String OTHER_APPLICATION_ID = "123e4567-e89b-12d3-a456-426614175002";
    private static final String ENVIRONMENT_ID = "123e4567-e89b-12d3-a456-426614175003";
    private static final String FUNCTIONALITY_ID = "123e4567-e89b-12d3-a456-426614175001";

    @Mock
    private EnvironmentRepository environmentRepository;
    @Mock
    private FunctionalityCatalogRepository functionalityCatalogRepository;
    @Mock
    private CatalogPort catalogPort;

    private CreateMessageContextRuleImpl rule;

    @BeforeEach
    void setUp() {
        rule = new CreateMessageContextRuleImpl(environmentRepository, functionalityCatalogRepository, catalogPort);
    }

    @Test
    void validate_acceptsContext_whenHierarchyIsConsistent() {
        CreateMessageDTO dto = validDto();
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment(APPLICATION_ID)));
        when(functionalityCatalogRepository.findAllByApplicationId(APPLICATION_ID))
                .thenReturn(List.of(functionality(FUNCTIONALITY_ID, APPLICATION_ID)));

        assertThatCode(() -> rule.validate(dto, ENVIRONMENT_ID)).doesNotThrowAnyException();
    }

    @Test
    void validate_acceptsContext_whenUuidRepresentationsUseDifferentCase() {
        CreateMessageDTO dto = validDto();
        dto.setEnvironmentId(ENVIRONMENT_ID.toUpperCase());
        dto.setApplicationId(APPLICATION_ID.toUpperCase());
        dto.setFunctionalityId(FUNCTIONALITY_ID.toUpperCase());
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment(APPLICATION_ID)));
        when(functionalityCatalogRepository.findAllByApplicationId(APPLICATION_ID))
                .thenReturn(List.of(functionality(FUNCTIONALITY_ID, APPLICATION_ID)));

        assertThatCode(() -> rule.validate(dto, ENVIRONMENT_ID)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void validate_rejectsContext_whenAuthenticatedEnvironmentIsMissing(String authenticatedEnvironmentId) {
        when(catalogPort.getMessage("FUN_145")).thenReturn("Environment not authorized");

        assertThatThrownBy(() -> rule.validate(validDto(), authenticatedEnvironmentId))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Environment not authorized", 403));
        verifyNoInteractions(environmentRepository, functionalityCatalogRepository);
    }

    @Test
    void validate_rejectsContext_whenRequestedEnvironmentDiffersFromAuthenticatedEnvironment() {
        CreateMessageDTO dto = validDto();
        dto.setEnvironmentId("123e4567-e89b-12d3-a456-426614175099");
        when(catalogPort.getMessage("FUN_145")).thenReturn("Environment not authorized");

        assertThatThrownBy(() -> rule.validate(dto, ENVIRONMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Environment not authorized", 403));
        verifyNoInteractions(environmentRepository, functionalityCatalogRepository);
    }

    @Test
    void validate_rejectsContext_whenEnvironmentDoesNotExist() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.empty());
        when(catalogPort.getMessage("FUN_035")).thenReturn("Environment does not exist");

        assertThatThrownBy(() -> rule.validate(validDto(), ENVIRONMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Environment does not exist", 403));
        verifyNoInteractions(functionalityCatalogRepository);
    }

    @Test
    void validate_rejectsContext_whenEnvironmentBelongsToAnotherApplication() {
        when(environmentRepository.findById(ENVIRONMENT_ID))
                .thenReturn(Optional.of(environment(OTHER_APPLICATION_ID)));
        when(catalogPort.getMessage("FUN_036")).thenReturn("Environment outside application");

        assertThatThrownBy(() -> rule.validate(validDto(), ENVIRONMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Environment outside application", 403));
        verifyNoInteractions(functionalityCatalogRepository);
    }

    @Test
    void validate_rejectsContext_whenFunctionalityDoesNotExistInApplication() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment(APPLICATION_ID)));
        when(functionalityCatalogRepository.findAllByApplicationId(APPLICATION_ID)).thenReturn(List.of());
        when(catalogPort.getMessage("FUN_146")).thenReturn("Functionality outside application");

        assertThatThrownBy(() -> rule.validate(validDto(), ENVIRONMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Functionality outside application", 403));
    }

    @Test
    void validate_rejectsContext_whenFunctionalityRepositoryReturnsNull() {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment(APPLICATION_ID)));
        when(functionalityCatalogRepository.findAllByApplicationId(APPLICATION_ID)).thenReturn(null);
        when(catalogPort.getMessage("FUN_146")).thenReturn("Functionality outside application");

        assertThatThrownBy(() -> rule.validate(validDto(), ENVIRONMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Functionality outside application", 403));
    }

    @ParameterizedTest
    @MethodSource("invalidFunctionalities")
    void validate_rejectsContext_whenFunctionalityIsOutsideApplication(FunctionalityData functionality) {
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment(APPLICATION_ID)));
        when(functionalityCatalogRepository.findAllByApplicationId(APPLICATION_ID))
                .thenReturn(Collections.singletonList(functionality));
        when(catalogPort.getMessage("FUN_146")).thenReturn("Functionality outside application");

        assertThatThrownBy(() -> rule.validate(validDto(), ENVIRONMENT_ID))
                .isInstanceOf(ForbiddenException.class)
                .satisfies(exception -> assertThat((ForbiddenException) exception)
                        .extracting(ForbiddenException::getUserMessage, ForbiddenException::getHttpStatus)
                        .containsExactly("Functionality outside application", 403));
    }

    private static Stream<FunctionalityData> invalidFunctionalities() {
        FunctionalityData withoutApplication = new FunctionalityData();
        withoutApplication.setId(UUID.fromString(FUNCTIONALITY_ID));
        return Stream.of(
                null,
                functionality("123e4567-e89b-12d3-a456-426614175098", APPLICATION_ID),
                withoutApplication,
                functionality(FUNCTIONALITY_ID, OTHER_APPLICATION_ID)
        );
    }

    private CreateMessageDTO validDto() {
        return CreateMessageDTO.builder()
                .applicationId(APPLICATION_ID)
                .environmentId(ENVIRONMENT_ID)
                .functionalityId(FUNCTIONALITY_ID)
                .build();
    }

    private EnvironmentData environment(String applicationId) {
        return new EnvironmentData(UUID.fromString(ENVIRONMENT_ID), "Environment",
                ApplicationData.build(UUID.fromString(applicationId), "Application"));
    }

    private static FunctionalityData functionality(String functionalityId, String applicationId) {
        return new FunctionalityData(UUID.fromString(functionalityId), "Functionality",
                ApplicationData.build(UUID.fromString(applicationId), "Application"), null, null);
    }
}
