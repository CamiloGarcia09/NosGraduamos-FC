package co.edu.uco.application.primaryports.facade.catalog.impl;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.usecase.handling.HandlingFindCatalogPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCatalogUseCaseFacadeImplTest {

    @Mock
    private HandlingFindCatalogPort handlingFindCatalogPort;

    private FindCatalogUseCaseFacadeImpl facade;

    @BeforeEach
    void setUp() {
        facade = new FindCatalogUseCaseFacadeImpl(handlingFindCatalogPort);
    }

    @Test
    void findApplications_delegatesToHandlingPort() {
        when(handlingFindCatalogPort.findApplications()).thenReturn(List.of(CatalogItemDTO.create("1", "App")));

        facade.findApplications();

        verify(handlingFindCatalogPort).findApplications();
    }

    @Test
    void findEnvironmentsByApplication_delegatesToHandlingPort() {
        facade.findEnvironmentsByApplication("app-1");

        verify(handlingFindCatalogPort).findEnvironmentsByApplication("app-1");
    }

    @Test
    void findFunctionalitiesByApplication_delegatesToHandlingPort() {
        facade.findFunctionalitiesByApplication("app-1");

        verify(handlingFindCatalogPort).findFunctionalitiesByApplication("app-1");
    }

    @Test
    void findMessageTypes_delegatesToHandlingPort() {
        facade.findMessageTypes();

        verify(handlingFindCatalogPort).findMessageTypes();
    }

    @Test
    void findMessageCategories_delegatesToHandlingPort() {
        facade.findMessageCategories();

        verify(handlingFindCatalogPort).findMessageCategories();
    }

    @Test
    void findMessageStates_delegatesToHandlingPort() {
        facade.findMessageStates();

        verify(handlingFindCatalogPort).findMessageStates();
    }

    @Test
    void findMessageEnvironmentStates_delegatesToHandlingPort() {
        facade.findMessageEnvironmentStates();

        verify(handlingFindCatalogPort).findMessageEnvironmentStates();
    }
}