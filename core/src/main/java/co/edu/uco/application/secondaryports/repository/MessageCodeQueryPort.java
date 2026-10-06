package co.edu.uco.application.secondaryports.repository;

public interface MessageCodeQueryPort {

    boolean existsByCodeAndApplicationId(String code, String applicationId);
}
