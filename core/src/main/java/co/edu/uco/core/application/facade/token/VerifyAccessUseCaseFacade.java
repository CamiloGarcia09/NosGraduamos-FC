package co.edu.uco.core.application.facade.token;

public interface VerifyAccessUseCaseFacade {
    boolean execute(String token);
}