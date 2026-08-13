package co.edu.uco.application.secondaryports.repository;

import co.edu.uco.application.secondaryports.entity.EnvironmentData;

import java.util.Optional;

public interface EnvironmentRepository {
    Optional<EnvironmentData> findById(String id);
}