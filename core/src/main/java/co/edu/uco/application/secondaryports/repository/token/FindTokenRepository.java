package co.edu.uco.application.secondaryports.repository.token;

import co.edu.uco.application.secondaryports.entity.TokenData;

import java.util.Optional;

public interface FindTokenRepository {
    TokenData findById(String id);
    Optional<TokenData> findByEnvironmentAndState(String environment, String state);
}