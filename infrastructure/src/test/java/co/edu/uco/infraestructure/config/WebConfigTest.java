package co.edu.uco.infraestructure.config;

import co.edu.uco.infraestructure.primaryadapters.interceptors.AcceptHeaderInterceptor;
import co.edu.uco.infraestructure.primaryadapters.interceptors.TokenHeaderInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static co.edu.uco.infraestructure.config.InfrastructureConstant.WEB_CONFIG_API_APPLICATION;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.WEB_CONFIG_API_CREATE_MESSAGE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.WEB_CONFIG_API_MESSAGE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.WEB_CONFIG_API_MESSAGE_CODE;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.WEB_CONFIG_API_MESSAGE_CODE_TRANSLATION;
import static co.edu.uco.infraestructure.config.InfrastructureConstant.WEB_CONFIG_API_MESSAGE_LIST;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private LoggingConfig loggingConfig;
    @Mock
    private AcceptHeaderInterceptor acceptHeaderInterceptor;
    @Mock
    private TokenHeaderInterceptor tokenHeaderInterceptor;
    @Mock
    private InterceptorRegistry registry;
    @Mock
    private InterceptorRegistration loggingRegistration;
    @Mock
    private InterceptorRegistration acceptHeaderRegistration;
    @Mock
    private InterceptorRegistration tokenHeaderRegistration;

    @Test
    void addInterceptors_protectsMessageListWithTokenInterceptor() {
        when(registry.addInterceptor(loggingConfig)).thenReturn(loggingRegistration);
        when(registry.addInterceptor(acceptHeaderInterceptor)).thenReturn(acceptHeaderRegistration);
        when(acceptHeaderRegistration.addPathPatterns("/messageucolab/v1/**"))
                .thenReturn(acceptHeaderRegistration);
        when(registry.addInterceptor(tokenHeaderInterceptor)).thenReturn(tokenHeaderRegistration);
        when(tokenHeaderRegistration.addPathPatterns(
                WEB_CONFIG_API_MESSAGE,
                WEB_CONFIG_API_APPLICATION,
                WEB_CONFIG_API_MESSAGE_LIST,
                WEB_CONFIG_API_MESSAGE_CODE,
                WEB_CONFIG_API_MESSAGE_CODE_TRANSLATION,
                WEB_CONFIG_API_CREATE_MESSAGE)).thenReturn(tokenHeaderRegistration);

        new WebConfig(loggingConfig, acceptHeaderInterceptor, tokenHeaderInterceptor).addInterceptors(registry);

        verify(tokenHeaderRegistration).addPathPatterns(
                WEB_CONFIG_API_MESSAGE,
                WEB_CONFIG_API_APPLICATION,
                WEB_CONFIG_API_MESSAGE_LIST,
                WEB_CONFIG_API_MESSAGE_CODE,
                WEB_CONFIG_API_MESSAGE_CODE_TRANSLATION,
                WEB_CONFIG_API_CREATE_MESSAGE);
        verify(tokenHeaderRegistration).order(0);
    }
}
