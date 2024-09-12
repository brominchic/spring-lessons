import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

@RequiredArgsConstructor
@Slf4j
public class CrudJpaControllerTest extends SpringBootApplicationTest {

    @Test
    void loginOkWithLogging() throws URISyntaxException, InterruptedException {
        postgreSQLContainer.withPassword("postgres").withDatabaseName("postgres");
        Thread.sleep(100000);
    }

}

