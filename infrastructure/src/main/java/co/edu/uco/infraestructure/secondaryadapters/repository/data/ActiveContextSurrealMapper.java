package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ActiveContextSurrealModel;
import org.springframework.stereotype.Component;

@Component
public final class ActiveContextSurrealMapper
        implements DataMapper<ActiveContextEntity, ActiveContextSurrealModel> {

    @Override
    public ActiveContextEntity mapperData(final ActiveContextSurrealModel model) {
        final ActiveContextEntity context = new ActiveContextEntity();
        context.setId(model.getId());
        context.setExternalIdentityId(model.getExternalIdentityId());
        context.setOrganizationId(model.getOrganizationId());
        context.setApplicationId(model.getApplicationId());
        context.setEnvironmentId(model.getEnvironmentId());
        context.setUpdatedAt(model.getUpdatedAt());
        return context;
    }

    @Override
    public ActiveContextSurrealModel mapperModel(final ActiveContextEntity context) {
        return new ActiveContextSurrealModel(
                context.getId(),
                context.getExternalIdentityId(),
                context.getOrganizationId(),
                context.getApplicationId(),
                context.getEnvironmentId(),
                context.getUpdatedAt());
    }
}
