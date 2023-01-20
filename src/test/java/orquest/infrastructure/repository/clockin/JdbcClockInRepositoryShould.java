package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.test.TestUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
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
            List.of(
                new ClockInAlert(
                    1L,
                    1L,
                    1L
                ),
                new ClockInAlert(
                    2L,
                    1L,
                    2L
                )
            )
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
                    186_400_500L,
                    ClockInRecordType.IN,
                    ClockInRecordAction.REST
                ),
                new ClockInRecord(
                    4L,
                    3L,
                    186_401_500L,
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
            List.of(
                new ClockInAlert(
                    3L,
                    4L,
                    2L
                )
            )
        );
    private final static ClockIn CLOCK_IN_FIVE =
        new ClockIn(
            5L,
            "businessId3",
            "employeeId3",
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
                CLOCK_IN_FOUR,
                CLOCK_IN_FIVE
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
    return_clock_ins_filtered_by_date() {
        ClockInFilter clockInFilter = new ClockInFilter.Builder().from(0L).to(86400000L).build();

        Assertions
            .assertThat(repository.find(clockInFilter))
            .containsExactlyInAnyOrder(
                CLOCK_IN_ONE
            );
    }
    
    @Test public void 
    return_clock_ins_filtered_by_business_id() {
        ClockInFilter clockInFilterOne = new ClockInFilter.Builder().businessIds(Set.of("businessId1", "businessId2")).build();
        ClockInFilter clockInFilterTwo = new ClockInFilter.Builder().businessIds(Set.of("businessId2")).build();

        Assertions
            .assertThat(repository.find(clockInFilterOne))
            .containsExactlyInAnyOrder(
                CLOCK_IN_ONE,
                CLOCK_IN_TWO,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR
            );

        Assertions
            .assertThat(repository.find(clockInFilterTwo))
            .containsExactlyInAnyOrder(
                CLOCK_IN_FOUR
            );
    }

    @Test public void
    return_clock_ins_filtered_by_employee_id() {
        ClockInFilter clockInFilterOne = new ClockInFilter.Builder().employeeIds(Set.of("employeeId1", "employeeId2")).build();
        ClockInFilter clockInFilterTwo = new ClockInFilter.Builder().employeeIds(Set.of("employeeId2")).build();

        Assertions
            .assertThat(repository.find(clockInFilterOne))
            .containsExactlyInAnyOrder(
                CLOCK_IN_ONE,
                CLOCK_IN_TWO,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR
            );

        Assertions
            .assertThat(repository.find(clockInFilterTwo))
            .containsExactlyInAnyOrder(
                CLOCK_IN_TWO
            );
    }

    private NamedParameterJdbcTemplate initTemplate(String schema) throws SQLException {
        return new NamedParameterJdbcTemplate(TestUtils.initDatabase(schema, "repository/clock_in/initial_data.sql"));
    }

    private String nextSchema() {
        return JdbcClockInRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}