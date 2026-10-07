package co.edu.uco.infraestructure.secondaryadapters.repository.data;

import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;
import co.edu.uco.infraestructure.secondaryadapters.repository.surreal.model.ExternalIdentitySurrealModel;
import org.springframework.stereotype.Component;

@Component
public final class ExternalIdentitySurrealMapper
        implements DataMapper<ExternalIdentityEntity, ExternalIdentitySurrealModel> {

    @Override
    public ExternalIdentityEntity mapperData(final ExternalIdentitySurrealModel model) {
        final ExternalIdentityEntity externalIdentity = new ExternalIdentityEntity();
        externalIdentity.setId(model.getId());
        externalIdentity.setIssuer(model.getIssuer());
        externalIdentity.setSubject(model.getSubject());
        externalIdentity.setEmail(model.getEmail());
        return externalIdentity;
    }

    @Override
    public ExternalIdentitySurrealModel mapperModel(final ExternalIdentityEntity externalIdentity) {
        return new ExternalIdentitySurrealModel(
                externalIdentity.getId(),
                externalIdentity.getIssuer(),
                externalIdentity.getSubject(),
                externalIdentity.getEmail());
    }
}
