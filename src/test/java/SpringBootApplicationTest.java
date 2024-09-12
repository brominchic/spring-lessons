import com.example.spring.ExampleApplication;
import com.example.spring.config.ApplicationConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
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
