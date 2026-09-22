package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

import java.util.List;

public interface HandlingFindCatalogPort {
    List<CatalogItemDTO> findApplications(ExternalIdentity identity);
    List<CatalogItemDTO> findEnvironmentsByApplication(String applicationId, ExternalIdentity identity);
    List<CatalogItemDTO> findFunctionalitiesByApplication(String applicationId, ExternalIdentity identity);
    List<CatalogItemDTO> findMessageTypes();
    List<CatalogItemDTO> findMessageCategories();
    List<CatalogItemDTO> findMessageStates();
    List<CatalogItemDTO> findMessageEnvironmentStates();
}
