package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;

import java.util.List;

public interface HandlingFindCatalogPort {
    List<CatalogItemDTO> findApplications();
    List<CatalogItemDTO> findEnvironmentsByApplication(String applicationId);
    List<CatalogItemDTO> findFunctionalitiesByApplication(String applicationId);
    List<CatalogItemDTO> findMessageTypes();
    List<CatalogItemDTO> findMessageCategories();
    List<CatalogItemDTO> findMessageStates();
    List<CatalogItemDTO> findMessageEnvironmentStates();
}
