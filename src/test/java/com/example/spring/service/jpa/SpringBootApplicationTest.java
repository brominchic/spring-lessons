package com.example.spring.service.jpa;

import com.example.spring.ExampleApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * General class for test containers.
 */
@SpringBootTest(classes = ExampleApplication.class)
@Testcontainers
@ActiveProfiles("test")
public class SpringBootApplicationTest {
    private static final String DATABASE_NAME = "spring-app";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME);

}
