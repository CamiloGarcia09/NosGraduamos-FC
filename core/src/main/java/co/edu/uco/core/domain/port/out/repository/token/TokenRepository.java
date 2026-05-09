package co.edu.uco.core.domain.port.out.repository.token;

import co.edu.uco.core.domain.data.TokenData;

public interface TokenRepository {
    TokenData save(TokenData tokenData);
}
