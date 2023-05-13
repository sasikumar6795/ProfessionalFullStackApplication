package com.sasicode.fullStackApp.testContainers;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
@Disabled
public abstract class TestContainers {

    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer  =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("sasicode-dao-unit-test")
                    .withUsername("sasicode")
                    .withPassword("password");


    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()).load();
        flyway.migrate();
    }

    @DynamicPropertySource
    private static void registerDatasourceProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        dynamicPropertyRegistry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        dynamicPropertyRegistry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

    protected static DataSource getDataSource() {
        DataSourceBuilder<?> builder = DataSourceBuilder
                .create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());
        return builder.build();
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    protected static final Faker FAKER = new Faker();
}
