package co.edu.uco.application.secondaryports.repository;

import co.edu.uco.application.secondaryports.entity.ApplicationData;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ApplicationRepository {

    Optional<ApplicationData> findByName(String name);

    Optional<ApplicationData> findById(String id);

    boolean existsById(String id);

    void create(ApplicationData application, String languageId, LocalDateTime startDate,
                LocalDateTime endDate, String stateId);
}
