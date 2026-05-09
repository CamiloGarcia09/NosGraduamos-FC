package co.edu.uco.core.domain.usecase.handling;

import co.edu.uco.core.domain.domains.TokenDomain;

public interface HandlingCreateTokenPort {
    void createToken(TokenDomain tokenDomain);
}
