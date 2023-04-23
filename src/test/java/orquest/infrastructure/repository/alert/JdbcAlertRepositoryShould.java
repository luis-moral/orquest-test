package orquest.infrastructure.repository.alert;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.alert.Alert;
import orquest.test.TestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcAlertRepositoryShould {

    private final static Alert ALERT_ONE =
        new Alert(
            UUID.fromString("2baa2295-27ee-4d60-9305-7e2f7e159988"),
            "1",
            "#clockIn.hasMatchedRecords() == false",
            "Missing clock ins"
        );
    private final static Alert ALERT_TWO =
        new Alert(
            UUID.fromString("35ac5f30-f5c4-475c-b7e8-194ae6396c25"),
            "1",
            "#clockIn.hasMatchedRecords() and #clockIn.timeWorked() > T(java.util.concurrent.TimeUnit).HOURS.toMillis(10)",
            "Maximum work hours exceeded"
        );
    private final static Alert ALERT_THREE =
        new Alert(
            UUID.fromString("7bee61e8-3c62-406c-a04a-d211b50b438e"),
            "1",
            "(#clockIn.dayOfWeek().isPresent()) and ((#clockIn.dayOfWeek().get() >= T(java.time.DayOfWeek).MONDAY && #clockIn.dayOfWeek().get() <= T(java.time.DayOfWeek).THURSDAY && #clockIn.firstRecordHourOfDay() < 8) or (#clockIn.dayOfWeek().get() == T(java.time.DayOfWeek).FRIDAY &&#clockIn.firstRecordHourOfDay() < 7))",
            "Invalid first clock in time"
        );

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private NamedParameterJdbcOperations jdbcTemplate;
    private JdbcAlertRepository repository;

    @BeforeEach
    public void setUp() throws SQLException {
        jdbcTemplate = new NamedParameterJdbcTemplate(initDataSource(nextSchema()));

        repository = new JdbcAlertRepository(jdbcTemplate, new JdbcAlertRepositoryMapper());
    }

    @Test public void
    return_alerts() {
        Assertions
            .assertThat(repository.find("1"))
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                ALERT_ONE,
                ALERT_TWO,
                ALERT_THREE
            );
    }

    @Test public void
    return_empty_list_if_no_alerts() {
        Assertions
            .assertThat(repository.find("2"))
            .isEmpty();
    }

    private DataSource initDataSource(String schema) throws SQLException {
        return TestUtils.initDatabase(schema);
    }

    private String nextSchema() {
        return JdbcAlertRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}