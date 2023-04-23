package orquest.infrastructure.repository.alert;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import orquest.domain.alert.Alert;
import orquest.domain.alert.AlertRepository;
import orquest.infrastructure.util.sql.SelectBuilder;

import java.util.List;

public class JdbcAlertRepository implements AlertRepository {

    private static SelectBuilder SelectAlertByBusinessId() {
        return
            new SelectBuilder()
                .field("id", "business_id", "expression", "message")
                .from("alert")
                .where("business_id = :business_id");
    }

    private final NamedParameterJdbcOperations jdbcTemplate;
    private final JdbcAlertRepositoryMapper mapper;

    public JdbcAlertRepository(NamedParameterJdbcOperations jdbcTemplate, JdbcAlertRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<Alert> find(String businessId) {
        return
            jdbcTemplate
                .query(
                    SelectAlertByBusinessId().toString(),
                    new MapSqlParameterSource("business_id", businessId),
                    mapper::toAlert
                );
    }
}
