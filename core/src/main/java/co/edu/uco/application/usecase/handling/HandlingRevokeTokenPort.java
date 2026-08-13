package co.edu.uco.application.usecase.handling;

public interface HandlingRevokeTokenPort {
    void execute(String environment, String state);
}