package co.edu.uco.application.secondaryports.repository.token;

import co.edu.uco.application.secondaryports.entity.TokenData;

public interface TokenRepository {
    TokenData save(TokenData tokenData);
}
