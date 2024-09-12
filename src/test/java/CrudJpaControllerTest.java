import com.example.spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
@Slf4j
public class CrudJpaControllerTest extends SpringBootApplicationTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void name() {
        assertTrue(postgreSQLContainer.isRunning());

        System.out.println(1);
    }
}

