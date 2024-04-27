package com.vedha.source.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.vedha.source.postgres.repository"}, // The package where the repository interfaces are located for the Postgres database, to create the repository beans
        entityManagerFactoryRef = "postgresEntityManagerFactory", // The name of the EntityManagerFactory bean for the Postgres database
        transactionManagerRef = "postgresTransactionManager" // The name of the TransactionManager bean for the Postgres database
)
public class PostgresDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgres") // Configuring the Postgres data source
    public DataSourceProperties postgresDataSourceProperties() { return new DataSourceProperties(); }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgres.hikari") // Configuring the Hikari connection pool
    public HikariDataSource postgresDataSource(@Qualifier("postgresDataSourceProperties") DataSourceProperties postgresDataSourceProperties) {
        // postgresDataSourceProperties spring bean is injected here as a parameter named as above bean name postgresDataSourceProperties,
        // So @qualifier is not necessary

        return postgresDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build(); // Creating a HikariDataSource object
    }

    // Create an EntityManagerFactory for the Postgres database
    @Bean
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(EntityManagerFactoryBuilder entityManagerFactoryBuilder, @Qualifier("postgresDataSource") HikariDataSource postgresDataSource) {

        return entityManagerFactoryBuilder
                .dataSource(postgresDataSource)
                .packages("com.vedha.source.postgres.entity") // The package where the entity classes are located for the Postgres database
                .persistenceUnit("postgres") // The name of the persistence unit for the Postgres database
                .build();
    }

    // Create a TransactionManager for the Postgres database
    // Passing the EntityManagerFactory bean as a parameter to the JpaTransactionManager constructor
    @Bean
    public PlatformTransactionManager postgresTransactionManager(@Qualifier("postgresEntityManagerFactory") LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory) {

        return new JpaTransactionManager(Objects.requireNonNull(postgresEntityManagerFactory.getObject()));
    }

    @Bean
    public JdbcTemplate postgresJdbcTemplate(@Qualifier("postgresDataSource") HikariDataSource postgresDataSource) {

        return new JdbcTemplate(postgresDataSource); // Creating a JdbcTemplate object for the Postgres database
    }

    @Bean
    public JdbcClient postgresJdbcClient(@Qualifier("postgresDataSource") HikariDataSource postgresDataSource) {

        return JdbcClient.create(postgresDataSource); // Creating a JdbcClient object for the Postgres database
    }
}
