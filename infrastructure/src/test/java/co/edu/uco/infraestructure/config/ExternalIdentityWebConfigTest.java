package co.edu.uco.infraestructure.config;

import co.edu.uco.infraestructure.primaryadapters.interceptors.ExternalIdentityInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExternalIdentityWebConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ExternalIdentityWebConfig.class)
            .withBean(ExternalIdentityInterceptor.class, () -> mock(ExternalIdentityInterceptor.class));

    @Test
    void configuration_isNotCreated_whenSimulatorIsNotExplicitlyEnabled() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(ExternalIdentityWebConfig.class));
    }

    @Test
    void configuration_isCreated_whenSimulatorIsExplicitlyEnabled() {
        contextRunner
                .withPropertyValues("components.security.simulated.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(ExternalIdentityWebConfig.class));
    }

    @Test
    void addInterceptors_registersExternalIdentityForApiRoutes() {
        var interceptor = mock(ExternalIdentityInterceptor.class);
        var registry = mock(InterceptorRegistry.class);
        var registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(interceptor)).thenReturn(registration);

        new ExternalIdentityWebConfig(interceptor).addInterceptors(registry);

        verify(registration).addPathPatterns("/messageucolab/v1/**");
    }
}
