package co.edu.uco.application.usecase;

import co.edu.uco.application.primaryports.dto.catalog.CatalogItemDTO;
import co.edu.uco.application.secondaryports.repository.ApplicationCatalogRepository;
import co.edu.uco.application.secondaryports.repository.EnvironmentCatalogRepository;
import co.edu.uco.application.secondaryports.repository.FunctionalityCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageTypeCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageCategoryCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageStateCatalogRepository;
import co.edu.uco.application.secondaryports.repository.MessageEnvironmentStateCatalogRepository;
import co.edu.uco.application.usecase.handling.HandlingFindCatalogPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class FindCatalogUseCase implements HandlingFindCatalogPort {

    private final ApplicationCatalogRepository applicationCatalogRepository;
    private final EnvironmentCatalogRepository environmentCatalogRepository;
    private final FunctionalityCatalogRepository functionalityCatalogRepository;
    private final MessageTypeCatalogRepository messageTypeCatalogRepository;
    private final MessageCategoryCatalogRepository messageCategoryCatalogRepository;
    private final MessageStateCatalogRepository messageStateCatalogRepository;
    private final MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository;

    public FindCatalogUseCase(
            ApplicationCatalogRepository applicationCatalogRepository,
            EnvironmentCatalogRepository environmentCatalogRepository,
            FunctionalityCatalogRepository functionalityCatalogRepository,
            MessageTypeCatalogRepository messageTypeCatalogRepository,
            MessageCategoryCatalogRepository messageCategoryCatalogRepository,
            MessageStateCatalogRepository messageStateCatalogRepository,
            MessageEnvironmentStateCatalogRepository messageEnvironmentStateCatalogRepository) {
        this.applicationCatalogRepository = applicationCatalogRepository;
        this.environmentCatalogRepository = environmentCatalogRepository;
        this.functionalityCatalogRepository = functionalityCatalogRepository;
        this.messageTypeCatalogRepository = messageTypeCatalogRepository;
        this.messageCategoryCatalogRepository = messageCategoryCatalogRepository;
        this.messageStateCatalogRepository = messageStateCatalogRepository;
        this.messageEnvironmentStateCatalogRepository = messageEnvironmentStateCatalogRepository;
    }

    @Override
    public List<CatalogItemDTO> findApplications() {
        return applicationCatalogRepository.findAll().stream()
                .map(app -> CatalogItemDTO.create(app.getId().toString(), app.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findEnvironmentsByApplication(String applicationId) {
        return environmentCatalogRepository.findAllByApplicationId(applicationId).stream()
                .map(env -> CatalogItemDTO.create(env.getId().toString(), env.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findFunctionalitiesByApplication(String applicationId) {
        return functionalityCatalogRepository.findAllByApplicationId(applicationId).stream()
                .map(func -> CatalogItemDTO.create(func.getId().toString(), func.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageTypes() {
        return messageTypeCatalogRepository.findAll().stream()
                .map(type -> CatalogItemDTO.create(type.getId().toString(), type.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageCategories() {
        return messageCategoryCatalogRepository.findAll().stream()
                .map(cat -> CatalogItemDTO.create(cat.getId().toString(), cat.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageStates() {
        return messageStateCatalogRepository.findAll().stream()
                .map(st -> CatalogItemDTO.create(st.getId().toString(), st.getName()))
                .toList();
    }

    @Override
    public List<CatalogItemDTO> findMessageEnvironmentStates() {
        return messageEnvironmentStateCatalogRepository.findAll().stream()
                .map(st -> CatalogItemDTO.create(st.getId().toString(), st.getName()))
                .toList();
    }
}
