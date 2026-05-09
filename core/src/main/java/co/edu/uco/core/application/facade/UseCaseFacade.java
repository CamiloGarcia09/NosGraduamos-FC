package co.edu.uco.core.application.facade;

public interface UseCaseFacade<T, R> {
    void execute(T dto, R response);
}
