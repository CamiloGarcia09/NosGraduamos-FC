package co.edu.uco.application.secondaryports.repository;

import co.edu.uco.application.usecase.domain.aggregate.entities.ExternalIdentityEntity;

import java.util.Optional;

public interface ExternalIdentityRepository {

    Optional<ExternalIdentityEntity> findByIssuerAndSubject(String issuer, String subject);

    void create(ExternalIdentityEntity externalIdentity);

    void update(ExternalIdentityEntity externalIdentity);
}
