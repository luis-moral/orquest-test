package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.ClockInRepository;
import orquest.infrastructure.repository.alert.JdbcAlertRepository;
import orquest.infrastructure.repository.alert.JdbcAlertRepositoryMapper;
import orquest.infrastructure.repository.clockin.JdbcClockInRepository;
import orquest.infrastructure.repository.clockin.JdbcClockInRepositoryMapper;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public AlertRepository alertRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcAlertRepository(jdbcTemplate, new JdbcAlertRepositoryMapper());
    }

    @Bean
    public ClockInRepository clockInRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcClockInRepository(jdbcTemplate, new JdbcClockInRepositoryMapper());
    }
}
