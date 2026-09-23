package co.edu.uco.application.secondaryports.repository;

import co.edu.uco.application.usecase.domain.aggregate.entities.ActiveContextEntity;

import java.util.Optional;
import java.util.UUID;

public interface ActiveContextRepository {

    Optional<ActiveContextEntity> findByExternalIdentityId(UUID externalIdentityId);

    void save(ActiveContextEntity activeContext);
}
