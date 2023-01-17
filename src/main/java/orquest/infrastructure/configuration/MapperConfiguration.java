package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.infrastructure.repository.JdbcClockInRepositoryMapper;

@Configuration
public class MapperConfiguration {

    @Bean
    public JdbcClockInRepositoryMapper jdbcClockInRepositoryMapper() {
        return new JdbcClockInRepositoryMapper();
    }
}
