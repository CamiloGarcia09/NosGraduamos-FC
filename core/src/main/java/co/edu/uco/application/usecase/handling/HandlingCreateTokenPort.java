package co.edu.uco.application.usecase.handling;

import co.edu.uco.application.usecase.domain.TokenDomain;

public interface HandlingCreateTokenPort {
    void createToken(TokenDomain tokenDomain);
}
