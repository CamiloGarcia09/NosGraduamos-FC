package co.edu.uco.core.domain.port.out.repository.token;

import co.edu.uco.core.domain.data.StatusTokenData;

public interface TokenStateRepository {
    StatusTokenData findByStatus(String id);
    StatusTokenData findByStatusName(String name);
}