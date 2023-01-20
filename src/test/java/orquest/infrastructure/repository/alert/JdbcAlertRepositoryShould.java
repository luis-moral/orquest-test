package orquest.infrastructure.repository.alert;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.alert.Alert;
import orquest.test.TestUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcAlertRepositoryShould {

    private final static Alert ALERT_ONE =
        new Alert(
            1L,
            "businessId1",
            "#clockIn.hasMatchedRecords()",
            "Missing clock ins"
        );
    private final static Alert ALERT_TWO =
        new Alert(
            2L,
            "businessId1",
            "#clockIn.timeWorked() > T(java.util.concurrent.TimeUnit).HOURS.toMillis(10)",
            "Maximum work hours exceeded"
        );
    private final static Alert ALERT_THREE =
        new Alert(
            3L,
            "businessId1",
            "(#clockIn.dayOfWeek().isPresent()) and ((#clockIn.dayOfWeek().get() >= T(java.time.DayOfWeek).MONDAY && #clockIn.dayOfWeek().get() <= T(java.time.DayOfWeek).THURSDAY && #clockIn.firstRecordHourOfDay() >= 8) or (#clockIn.dayOfWeek().get() == T(java.time.DayOfWeek).FRIDAY &&#clockIn.firstRecordHourOfDay() >= 7))",
            "Invalid first clock in time"
        );

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private NamedParameterJdbcTemplate jdbcTemplate;
    private JdbcAlertRepository repository;

    @BeforeEach
    public void setUp() throws SQLException {
        jdbcTemplate = initTemplate(nextSchema());

        repository = new JdbcAlertRepository(jdbcTemplate, new JdbcAlertRepositoryMapper());
    }

    @Test public void
    return_alerts() {
        Assertions
            .assertThat(repository.find("businessId1"))
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
            .assertThat(repository.find("businessId2"))
            .isEmpty();
    }

    private NamedParameterJdbcTemplate initTemplate(String schema) throws SQLException {
        return new NamedParameterJdbcTemplate(TestUtils.initDatabase(schema));
    }

    private String nextSchema() {
        return JdbcAlertRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}