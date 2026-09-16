package co.edu.uco.infraestructure;

import co.edu.uco.init.CrossWordApplication;
import com.surrealdb.Surreal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CrossWordApplication.class)
@ActiveProfiles("test")
class InfrastructureApplicationTests {

    @MockBean
    private Surreal surreal;

    @Autowired
    private Clock clock;

    @Test
    void contextLoads() {
        assertThat(clock.getZone()).isEqualTo(ZoneOffset.UTC);
    }
}
