package co.edu.uco.core.domain.usecase.handling;

public interface HandlingRevokeTokenPort {
    void execute(String environment, String state);
}