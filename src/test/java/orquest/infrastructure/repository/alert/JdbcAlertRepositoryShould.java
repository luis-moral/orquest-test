package orquest.infrastructure.repository.alert;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.infrastructure.repository.clockin.JdbcClockInRepositoryShould;
import orquest.test.TestUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcAlertRepositoryShould {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private NamedParameterJdbcTemplate jdbcTemplate;
    private JdbcAlertRepository repository;

    @BeforeEach
    public void setUp() throws SQLException {
        jdbcTemplate = initTemplate(nextSchema());

        repository = new JdbcAlertRepository(jdbcTemplate, new JdbcAlertRepositoryMapper());
    }

    private NamedParameterJdbcTemplate initTemplate(String schema) throws SQLException {
        return new NamedParameterJdbcTemplate(TestUtils.initDatabase(schema, "repository/clock_in/initial_data.sql"));
    }

    private String nextSchema() {
        return JdbcClockInRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}