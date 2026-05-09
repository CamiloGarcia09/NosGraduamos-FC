package co.edu.uco.infrastructure.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;

@Configuration
@EnableJpaRepositories(basePackages=PACKAGE_REPOSITORY_POSTGRESQL_ADAPTER)
public class JpaConfig {
    private final DatabaseProperties databaseProperties;
    public JpaConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(JPA_DRIVER_CLASS_NAME);
        dataSource.setUrl(databaseProperties.getUrl());
        dataSource.setUsername(databaseProperties.getUsername());
        dataSource.setPassword(databaseProperties.getPassword());
        return dataSource;
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(PACKAGE_REPOSITORY_ADAPTER_ENTITY);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        
        emf.getJpaPropertyMap().put(JPA_HIBERNATE_DIALECT, databaseProperties.getDialect());
        emf.getJpaPropertyMap().put(JPA_HIBERNATE_SHOW_SQL, databaseProperties.isShowSql());
        return emf;
    }
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}