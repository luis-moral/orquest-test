package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.CreateClockInRecord;
import orquest.domain.time.TimeRecordType;
import orquest.test.TestUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcClockInRepositoryShould {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private final static UUID ALERT_ONE_ID = UUID.fromString("2baa2295-27ee-4d60-9305-7e2f7e159988");
    private final static UUID ALERT_TWO_ID = UUID.fromString("35ac5f30-f5c4-475c-b7e8-194ae6396c25");

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
                    TimeRecordType.IN,
                    ClockInRecordAction.WORK
                ),
                new ClockInRecord(
                    2L,
                    1L,
                    15_500L,
                    TimeRecordType.OUT,
                    ClockInRecordAction.WORK
                )
            ),
            List.of(
                new ClockInAlert(
                    1L,
                    1L,
                    ALERT_ONE_ID
                ),
                new ClockInAlert(
                    2L,
                    1L,
                    ALERT_TWO_ID
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
                    TimeRecordType.IN,
                    ClockInRecordAction.REST
                ),
                new ClockInRecord(
                    4L,
                    3L,
                    186_401_500L,
                    TimeRecordType.OUT,
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
                    ALERT_TWO_ID
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
    return_empty_list_of_ni_clock_ins() {
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

    @Test public void
    create_clock_ins() {
        CreateClockInRecord clockInRecordOne = new CreateClockInRecord(12_500L, TimeRecordType.IN, ClockInRecordAction.WORK);
        CreateClockInRecord clockInRecordTwo = new CreateClockInRecord(15_500L, TimeRecordType.OUT, ClockInRecordAction.WORK);
        CreateClockInAlert createClockInAlertOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert createClockInAlertTwo = new CreateClockInAlert(ALERT_TWO_ID);

        CreateClockIn createClockInOne = new CreateClockIn("businessId2", "employeeId3", "serviceId3", List.of(clockInRecordOne, clockInRecordTwo), List.of(createClockInAlertOne));
        CreateClockIn createClockInTwo = new CreateClockIn("businessId1", "employeeId4", "serviceId9", List.of(), List.of(createClockInAlertTwo));

        ClockInRecord clockInRecordFiveOne = new ClockInRecord(5L, 5L, 12_500L, TimeRecordType.IN, ClockInRecordAction.WORK);
        ClockInRecord clockInRecordFiveTwo = new ClockInRecord(6L, 5L, 15_500L, TimeRecordType.OUT, ClockInRecordAction.WORK);
        ClockIn expectedClockInFive = new ClockIn(5L, "businessId2", "employeeId3", "serviceId3", List.of(clockInRecordFiveOne, clockInRecordFiveTwo), List.of(new ClockInAlert(4L, 5L, ALERT_ONE_ID)));
        ClockIn expectedClockInSix = new ClockIn(6L, "businessId2", "employeeId3", "serviceId3", List.of(), List.of(new ClockInAlert(5L, 6L, ALERT_TWO_ID)));

        Assertions
            .assertThat(repository.create(List.of(createClockInOne, createClockInTwo)))
            .isEqualTo(2);

        Assertions
            .assertThat(repository.find())
            .containsExactly(
                CLOCK_IN_ONE,
                CLOCK_IN_TWO,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR,
                expectedClockInFive,
                expectedClockInSix
            );
    }

    private NamedParameterJdbcTemplate initTemplate(String schema) throws SQLException {
        return new NamedParameterJdbcTemplate(TestUtils.initDatabase(schema, "repository/clock_in/initial_data.sql"));
    }

    private String nextSchema() {
        return JdbcClockInRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}