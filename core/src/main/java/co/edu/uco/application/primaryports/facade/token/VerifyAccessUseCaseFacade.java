package co.edu.uco.application.primaryports.facade.token;

public interface VerifyAccessUseCaseFacade {
    boolean execute(String token);
}