package co.edu.uco.infraestructure;

import co.edu.uco.init.CrossWordApplication;
import com.surrealdb.Surreal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = CrossWordApplication.class)
@ActiveProfiles("test")
class InfrastructureApplicationTests {

    @MockBean
    private Surreal surreal;

    @Test
    void contextLoads() {
    }
}
