package co.edu.uco.application.secondaryports.repository.token;

import co.edu.uco.application.secondaryports.entity.StatusTokenData;

public interface TokenStateRepository {
    StatusTokenData findByStatus(String id);
    StatusTokenData findByStatusName(String name);
}