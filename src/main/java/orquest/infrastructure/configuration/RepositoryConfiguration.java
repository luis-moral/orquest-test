package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.infrastructure.repository.JdbcClockInRepository;
import orquest.infrastructure.repository.JdbcClockInRepositoryMapper;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public JdbcClockInRepository clockInRepository(
        NamedParameterJdbcTemplate jdbcTemplate,
        JdbcClockInRepositoryMapper jdbcClockInRepositoryMapper
    ) {
        return new JdbcClockInRepository(jdbcTemplate, jdbcClockInRepositoryMapper);
    }
}
