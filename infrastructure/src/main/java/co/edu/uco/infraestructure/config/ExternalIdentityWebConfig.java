package co.edu.uco.infraestructure.config;

import co.edu.uco.infraestructure.primaryadapters.interceptors.ExternalIdentityInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(
        prefix = "components.security.simulated",
        name = "enabled",
        havingValue = "true"
)
public class ExternalIdentityWebConfig implements WebMvcConfigurer {

    private final ExternalIdentityInterceptor externalIdentityInterceptor;

    public ExternalIdentityWebConfig(ExternalIdentityInterceptor externalIdentityInterceptor) {
        this.externalIdentityInterceptor = externalIdentityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(externalIdentityInterceptor)
                .addPathPatterns("/messageucolab/v1/**");
    }
}
