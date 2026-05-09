package co.edu.uco.core.domain.port;

public interface GenericPort <T, R> {
    void execute(T dto, R response);
}
