package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.ClockInRepository;
import orquest.infrastructure.repository.alert.JdbcAlertRepository;
import orquest.infrastructure.repository.alert.JdbcAlertRepositoryMapper;
import orquest.infrastructure.repository.clockin.JdbcClockInRepository;
import orquest.infrastructure.repository.clockin.JdbcClockInRepositoryMapper;
import orquest.infrastructure.util.generator.IdGenerator;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    public AlertRepository alertRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcAlertRepository(jdbcTemplate, new JdbcAlertRepositoryMapper());
    }

    @Bean
    public ClockInRepository clockInRepository(
        NamedParameterJdbcTemplate jdbcTemplate,
        TransactionTemplate transactionTemplate,
        IdGenerator idGenerator
    ) {
        return new JdbcClockInRepository(jdbcTemplate, transactionTemplate, new JdbcClockInRepositoryMapper(), idGenerator);
    }
}
