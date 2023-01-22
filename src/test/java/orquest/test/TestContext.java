package orquest.test;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import orquest.infrastructure.util.generator.IdGenerator;

@TestConfiguration
public class TestContext {

    @Bean
    public IdGenerator idGenerator() {
        return Mockito.mock(IdGenerator.class);
    }
}
