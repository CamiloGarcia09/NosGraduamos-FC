package co.edu.uco.infrastructure.adapter.primary;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface FindMessagesController {
        void findByEnvironmentAndMessage(String page, String size, String sort, String columnSort, String token,
                                         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
        void findByCodeMessageAndEnvironment(String messageCode, HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse);
}