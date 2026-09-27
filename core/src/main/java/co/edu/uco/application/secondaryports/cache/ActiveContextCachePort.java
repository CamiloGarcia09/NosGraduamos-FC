package co.edu.uco.application.secondaryports.cache;

import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;
import co.edu.uco.application.usecase.domain.security.ExternalIdentity;

import java.util.Optional;

public interface ActiveContextCachePort {

    Optional<ActiveContextEntity> find(ExternalIdentity externalIdentity);

    void save(ExternalIdentity externalIdentity, ActiveContextEntity activeContext);

    void evict(ExternalIdentity externalIdentity);
}
