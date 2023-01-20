package orquest.infrastructure.repository.alert;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.alert.Alert;
import orquest.domain.alert.AlertRepository;

import java.util.List;

public class JdbcAlertRepository implements AlertRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcAlertRepositoryMapper mapper;

    public JdbcAlertRepository(NamedParameterJdbcTemplate jdbcTemplate, JdbcAlertRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<Alert> find(String businessId) {
        throw new UnsupportedOperationException();
    }
}
