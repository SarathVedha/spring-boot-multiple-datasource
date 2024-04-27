package com.vedha.source.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.vedha.source.mysql.repository"}, // The package where the repository interfaces are located for the MySQL database, to create the repository beans
        entityManagerFactoryRef = "mysqlEntityManagerFactory", // The name of the EntityManagerFactory bean for the MySQL database
        transactionManagerRef = "mysqlTransactionManager" // The name of the TransactionManager bean for the MySQL database
)
@EnableTransactionManagement
public class MySqlDataSourceConfig {

    // Create a DataSourceProperties for the MySQL database
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mysql") // Configuring the MySQL data source
    public DataSourceProperties mysqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    // Create a HikariDataSource for the MySQL database
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql.hikari") // Configuring the Hikari connection pool
    public HikariDataSource mysqlDataSource(@Qualifier("mysqlDataSourceProperties") DataSourceProperties mysqlDataSourceProperties) {
        // mysqlDataSourceProperties spring bean is injected here as a parameter named as above bean name mysqlDataSourceProperties,
        // So @qualifier is not necessary

        return mysqlDataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build(); // Creating a HikariDataSource object
    }

    // Create an EntityManagerFactory for the MySQL database
    // An EntityManager is an interface that is used to interact with the persistence context.
    // It is used to create and remove persistent entity instances, to find entities by their primary key, and to query over entities.
    @Bean
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder entityManagerFactoryBuilder, @Qualifier("mysqlDataSource") HikariDataSource mysqlDataSource) {

        return entityManagerFactoryBuilder
                .dataSource(mysqlDataSource)
                .packages("com.vedha.source.mysql.entity") // The package where the entity classes are located for the MySQL database
                .persistenceUnit("mysql") // The name of the persistence unit for the MySQL database
                .build();
    }

    // Create a TransactionManager for the MySQL database
    // Passing the EntityManagerFactory bean as a parameter to the JpaTransactionManager constructor
    @Bean
    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("mysqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory) {

        return new JpaTransactionManager(Objects.requireNonNull(mysqlEntityManagerFactory.getObject()));
    }

    @Bean
    public JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlDataSource") HikariDataSource mysqlDataSource) {

        return new JdbcTemplate(mysqlDataSource); // Creating a JdbcTemplate object for the MySQL database
    }

    @Bean
    public JdbcClient mysqlJdbcClient(@Qualifier("mysqlDataSource") HikariDataSource mysqlDataSource) {

        return JdbcClient.create(mysqlDataSource); // Creating a JdbcClient object for the MySQL database
    }
}
