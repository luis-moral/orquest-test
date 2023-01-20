package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.test.TestUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcClockInRepositoryShould {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private final static ClockIn CLOCK_IN_ONE =
        new ClockIn(
            1L,
            "businessId1",
            "employeeId1",
            "serviceId1",
            List.of(
                new ClockInRecord(
                    1L,
                    1L,
                    10_500L,
                    ClockInRecordType.IN,
                    ClockInRecordAction.WORK
                ),
                new ClockInRecord(
                    2L,
                    1L,
                    15_500L,
                    ClockInRecordType.OUT,
                    ClockInRecordAction.WORK
                )
            ),
            List.of()
        );
    private final static ClockIn CLOCK_IN_TWO =
        new ClockIn(
            2L,
            "businessId1",
            "employeeId2",
            "serviceId1",
            List.of(),
            List.of()
        );
    private final static ClockIn CLOCK_IN_THREE =
        new ClockIn(
            3L,
            "businessId1",
            "employeeId1",
            "serviceId2",
            List.of(
                new ClockInRecord(
                    3L,
                    3L,
                    15_500L,
                    ClockInRecordType.IN,
                    ClockInRecordAction.REST
                ),
                new ClockInRecord(
                    4L,
                    3L,
                    25_500L,
                    ClockInRecordType.OUT,
                    ClockInRecordAction.REST
                )
            ),
            List.of()
        );
    private final static ClockIn CLOCK_IN_FOUR =
        new ClockIn(
            4L,
            "businessId2",
            "employeeId1",
            "serviceId1",
            List.of(),
            List.of()
        );

    private NamedParameterJdbcTemplate jdbcTemplate;
    private JdbcClockInRepository repository;

    @BeforeEach
    public void setUp() throws SQLException {
        jdbcTemplate = initTemplate(nextSchema());

        repository = new JdbcClockInRepository(jdbcTemplate, new JdbcClockInRepositoryMapper());
    }

    @Test public void
    return_clock_ins_not_filtered() {
        Assertions
            .assertThat(repository.find())
            .containsExactlyInAnyOrder(
                CLOCK_IN_ONE,
                CLOCK_IN_TWO,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR
            );
    }

    @Test public void
    return_empty_list_of_no_clock_ins() {
        jdbcTemplate.getJdbcOperations().update("DELETE FROM clock_in");

        Assertions
            .assertThat(repository.find())
            .isEmpty();
    }
    
    @Test public void 
    return_clock_ins_filtered_by_business_id() {
    }

    private NamedParameterJdbcTemplate initTemplate(String schema) throws SQLException {
        return new NamedParameterJdbcTemplate(TestUtils.initDatabase(schema, "repository/clock_in/clock_in.sql"));
    }

    private String nextSchema() {
        return JdbcClockInRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}