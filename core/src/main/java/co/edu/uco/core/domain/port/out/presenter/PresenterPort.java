package co.edu.uco.core.domain.port.out.presenter;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface PresenterPort<T> {
    void presentRestSuccess(List<T> dto, HttpServletRequest request, HttpServletResponse response);
}