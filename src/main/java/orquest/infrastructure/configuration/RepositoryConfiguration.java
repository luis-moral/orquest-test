package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.record.ClockInRecordRepository;
import orquest.infrastructure.repository.JdbcAlertRepository;
import orquest.infrastructure.repository.JdbcClockInRecordRepository;
import orquest.infrastructure.repository.JdbcClockInRepository;
import orquest.infrastructure.repository.JdbcClockInRepositoryMapper;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public AlertRepository alertRepository() {
        return new JdbcAlertRepository();
    }

    @Bean
    public ClockInRecordRepository clockInRecordRepository() {
        return new JdbcClockInRecordRepository();
    }

    @Bean
    public ClockInRepository clockInRepository(
        NamedParameterJdbcTemplate jdbcTemplate,
        JdbcClockInRepositoryMapper jdbcClockInRepositoryMapper
    ) {
        return new JdbcClockInRepository(jdbcTemplate, jdbcClockInRepositoryMapper);
    }
}
