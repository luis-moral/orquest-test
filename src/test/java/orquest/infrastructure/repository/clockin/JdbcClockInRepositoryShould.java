package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import orquest.infrastructure.util.generator.IdGenerator;
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

    private final static UUID CLOCK_IN_ONE_ID = UUID.fromString("2d6d2267-9c2c-437b-a763-96e986bd8d84");
    private final static UUID CLOCK_IN_TWO_ID = UUID.fromString("af0a0ef1-9974-4c64-968f-347a5568d5ca");
    private final static UUID CLOCK_IN_THREE_ID = UUID.fromString("52cd4d03-2875-4530-96bf-1c5620fdeed2");
    private final static UUID CLOCK_IN_FOUR_ID = UUID.fromString("eec008e8-a5b5-4458-a391-66f21d9aa3cc");
    private final static UUID CLOCK_IN_FIVE_ID = UUID.fromString("54d0ef14-6264-4216-9374-8762adb691f0");

    private final static ClockIn CLOCK_IN_ONE =
        new ClockIn(
            CLOCK_IN_ONE_ID,
            "businessId1",
            "employeeId1",
            "serviceId1",
            List.of(
                new ClockInRecord(
                    CLOCK_IN_ONE_ID,
                    10_500L,
                    TimeRecordType.IN,
                    ClockInRecordAction.WORK
                ),
                new ClockInRecord(
                    CLOCK_IN_ONE_ID,
                    15_500L,
                    TimeRecordType.OUT,
                    ClockInRecordAction.WORK
                )
            ),
            List.of(
                new ClockInAlert(
                    CLOCK_IN_ONE_ID,
                    ALERT_ONE_ID
                ),
                new ClockInAlert(
                    CLOCK_IN_ONE_ID,
                    ALERT_TWO_ID
                )
            )
        );
    private final static ClockIn CLOCK_IN_TWO =
        new ClockIn(
            CLOCK_IN_TWO_ID,
            "businessId1",
            "employeeId2",
            "serviceId1",
            List.of(),
            List.of()
        );
    private final static ClockIn CLOCK_IN_THREE =
        new ClockIn(
            CLOCK_IN_THREE_ID,
            "businessId1",
            "employeeId1",
            "serviceId2",
            List.of(
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    186_400_500L,
                    TimeRecordType.IN,
                    ClockInRecordAction.REST
                ),
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    186_401_500L,
                    TimeRecordType.OUT,
                    ClockInRecordAction.REST
                )
            ),
            List.of()
        );
    private final static ClockIn CLOCK_IN_FOUR =
        new ClockIn(
            CLOCK_IN_FOUR_ID,
            "businessId2",
            "employeeId1",
            "serviceId1",
            List.of(),
            List.of(
                new ClockInAlert(
                    CLOCK_IN_FOUR_ID,
                    ALERT_TWO_ID
                )
            )
        );
    private final static ClockIn CLOCK_IN_FIVE =
        new ClockIn(
            CLOCK_IN_FIVE_ID,
            "businessId3",
            "employeeId3",
            "serviceId1",
            List.of(),
            List.of()
        );

    private NamedParameterJdbcTemplate jdbcTemplate;
    private IdGenerator idGenerator;
    private JdbcClockInRepository repository;

    @BeforeEach
    public void setUp() throws SQLException {
        jdbcTemplate = initTemplate(nextSchema());
        idGenerator = Mockito.mock(IdGenerator.class);

        repository = new JdbcClockInRepository(jdbcTemplate, new JdbcClockInRepositoryMapper(), idGenerator);
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
        UUID newClockInOneId = UUID.randomUUID();
        UUID newClockInTwoId = UUID.randomUUID();

        CreateClockInRecord clockInRecordOne = new CreateClockInRecord(12_500L, TimeRecordType.IN, ClockInRecordAction.WORK);
        CreateClockInRecord clockInRecordTwo = new CreateClockInRecord(15_500L, TimeRecordType.OUT, ClockInRecordAction.WORK);
        CreateClockInAlert createClockInAlertOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert createClockInAlertTwo = new CreateClockInAlert(ALERT_TWO_ID);

        CreateClockIn createClockInOne = new CreateClockIn("businessId2", "employeeId3", "serviceId3", List.of(clockInRecordOne, clockInRecordTwo), List.of(createClockInAlertOne));
        CreateClockIn createClockInTwo = new CreateClockIn("businessId1", "employeeId4", "serviceId9", List.of(), List.of(createClockInAlertOne, createClockInAlertTwo));

        ClockInRecord clockInRecordFiveOne = new ClockInRecord(newClockInOneId, 12_500L, TimeRecordType.IN, ClockInRecordAction.WORK);
        ClockInRecord clockInRecordFiveTwo = new ClockInRecord(newClockInOneId, 15_500L, TimeRecordType.OUT, ClockInRecordAction.WORK);
        ClockIn expectedClockInFive = new ClockIn(newClockInOneId, "businessId2", "employeeId3", "serviceId3", List.of(clockInRecordFiveOne, clockInRecordFiveTwo), List.of(new ClockInAlert(newClockInOneId, ALERT_ONE_ID)));
        ClockIn expectedClockInSix = new ClockIn(newClockInTwoId, "businessId2", "employeeId3", "serviceId3", List.of(), List.of(new ClockInAlert(newClockInTwoId, ALERT_ONE_ID), new ClockInAlert(newClockInTwoId, ALERT_TWO_ID)));

        Mockito
            .when(idGenerator.generateId())
            .thenReturn(newClockInOneId, newClockInTwoId);

        Assertions
            .assertThat(repository.create(List.of(createClockInOne, createClockInTwo)))
            .isEqualTo(2);

        Assertions
            .assertThat(repository.find())
            .containsExactlyInAnyOrder(
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