import com.smomic.config.DatabaseConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class ClasSpdbTest {

    @Test
    public void testConnection() {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(DatabaseConfig.class);
        context.close();
    }
}
