package co.edu.uco.infraestructure.primaryadapters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MessagesController {
        void findByEnvironmentAndMessage(String page, String size, String sort, String columnSort, String token,
                                         HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
        void findByCodeMessageAndEnvironment(String messageCode, HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse);
        void translateByCodeMessageAndEnvironment(
                        String messageCode,
                        String sourceLanguage,
                        String targetLanguage,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse);
}
