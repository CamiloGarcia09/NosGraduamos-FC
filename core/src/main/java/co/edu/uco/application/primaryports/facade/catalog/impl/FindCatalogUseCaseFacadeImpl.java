package co.edu.uco.application.primaryports.facade.catalog.impl;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.primaryports.facade.catalog.FindCatalogUseCaseFacade;
import co.edu.uco.application.usecase.handling.HandlingFindCatalogPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class FindCatalogUseCaseFacadeImpl implements FindCatalogUseCaseFacade {

    private final HandlingFindCatalogPort handlingFindCatalogPort;

    public FindCatalogUseCaseFacadeImpl(HandlingFindCatalogPort handlingFindCatalogPort) {
        this.handlingFindCatalogPort = handlingFindCatalogPort;
    }

    @Override
    public List<CatalogItemDTO> findApplications() {
        return handlingFindCatalogPort.findApplications();
    }

    @Override
    public List<CatalogItemDTO> findEnvironmentsByApplication(String applicationId) {
        return handlingFindCatalogPort.findEnvironmentsByApplication(applicationId);
    }

    @Override
    public List<CatalogItemDTO> findFunctionalitiesByApplication(String applicationId) {
        return handlingFindCatalogPort.findFunctionalitiesByApplication(applicationId);
    }

    @Override
    public List<CatalogItemDTO> findMessageTypes() {
        return handlingFindCatalogPort.findMessageTypes();
    }

    @Override
    public List<CatalogItemDTO> findMessageCategories() {
        return handlingFindCatalogPort.findMessageCategories();
    }

    @Override
    public List<CatalogItemDTO> findMessageStates() {
        return handlingFindCatalogPort.findMessageStates();
    }

    @Override
    public List<CatalogItemDTO> findMessageEnvironmentStates() {
        return handlingFindCatalogPort.findMessageEnvironmentStates();
    }
}
