package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.record.ClockInRecordRepository;
import orquest.infrastructure.repository.JdbcClockInRepository;
import orquest.infrastructure.repository.JdbcClockInRepositoryMapper;
import orquest.infrastructure.repository.JdbcClockInRecordRepository;

@Configuration
public class RepositoryConfiguration {

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
