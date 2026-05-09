package co.edu.uco.core.domain.port.out.repository;

import co.edu.uco.core.domain.data.EnvironmentData;

import java.util.Optional;

public interface EnvironmentRepository {
    Optional<EnvironmentData> findById(String id);
}