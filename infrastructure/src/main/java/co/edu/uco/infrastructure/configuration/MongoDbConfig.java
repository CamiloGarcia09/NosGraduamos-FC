package co.edu.uco.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.PACKAGE_REPOSITORY_ADAPTER;

@Configuration
@EnableMongoRepositories(basePackages = {PACKAGE_REPOSITORY_ADAPTER})
public class MongoDbConfig {
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new LongToLocalDateTimeConverter(),
                new LocalDateTimeToLongConverter()
        ));
    }
}