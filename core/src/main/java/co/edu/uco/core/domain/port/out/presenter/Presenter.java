package co.edu.uco.core.domain.port.out.presenter;

public interface Presenter<T> {
    void present(T dto);
}