package co.edu.uco.core.domain.port.out.repository.token;

import co.edu.uco.core.domain.data.TokenData;

import java.util.Optional;

public interface FindTokenRepository {
    TokenData findById(String id);
    Optional<TokenData> findByEnvironmentAndState(String environment, String state);
}