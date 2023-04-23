package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.CreateClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;
import orquest.infrastructure.util.generator.IdGenerator;
import orquest.test.TestUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcClockInRepositoryShould {

    private final static AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private final static UUID ALERT_ONE_ID = UUID.fromString("2baa2295-27ee-4d60-9305-7e2f7e159988");
    private final static UUID ALERT_TWO_ID = UUID.fromString("35ac5f30-f5c4-475c-b7e8-194ae6396c25");
    private final static UUID ALERT_THREE_ID = UUID.fromString("7bee61e8-3c62-406c-a04a-d211b50b438e");

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
                    TimeRecordAction.WORK
                ),
                new ClockInRecord(
                    CLOCK_IN_ONE_ID,
                    15_500L,
                    TimeRecordType.OUT,
                    TimeRecordAction.WORK
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
                    TimeRecordAction.REST
                ),
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    186_401_500L,
                    TimeRecordType.OUT,
                    TimeRecordAction.REST
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

    private NamedParameterJdbcOperations jdbcTemplate;
    private IdGenerator idGenerator;
    private JdbcClockInRepository repository;

    @BeforeEach
    public void setUp() throws SQLException {
        DataSource dataSource = initDataSource(nextSchema());
        TransactionTemplate transactionTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));

        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        idGenerator = Mockito.mock(IdGenerator.class);

        repository = new JdbcClockInRepository(jdbcTemplate, transactionTemplate, new JdbcClockInRepositoryMapper(), idGenerator);
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

    @Test public void
    create_clock_ins() {
        UUID newClockInOneId = UUID.randomUUID();
        UUID newClockInTwoId = UUID.randomUUID();

        CreateClockInRecord createRecordOneOne = new CreateClockInRecord(12_500L, TimeRecordType.IN, TimeRecordAction.WORK);
        CreateClockInRecord createRecordOneTwo = new CreateClockInRecord(15_500L, TimeRecordType.OUT, TimeRecordAction.WORK);
        CreateClockInAlert createAlertOneOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert createAlertTwoOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert createAlertTwoTwo = new CreateClockInAlert(ALERT_TWO_ID);

        CreateClockIn createClockInOne = new CreateClockIn("businessId2", "employeeId3", "serviceId3", List.of(createRecordOneOne, createRecordOneTwo), List.of(createAlertOneOne));
        CreateClockIn createClockInTwo = new CreateClockIn("businessId1", "employeeId4", "serviceId9", List.of(), List.of(createAlertTwoOne, createAlertTwoTwo));

        ClockInRecord expectedRecordOneOne = new ClockInRecord(newClockInOneId, 12_500L, TimeRecordType.IN, TimeRecordAction.WORK);
        ClockInRecord expectedRecordTwoTwo = new ClockInRecord(newClockInOneId, 15_500L, TimeRecordType.OUT, TimeRecordAction.WORK);
        ClockIn expectedClockInOne = new ClockIn(newClockInOneId, "businessId2", "employeeId3", "serviceId3", List.of(expectedRecordOneOne, expectedRecordTwoTwo), List.of(new ClockInAlert(newClockInOneId, ALERT_ONE_ID)));
        ClockIn expectedClockInTwo = new ClockIn(newClockInTwoId, "businessId1", "employeeId4", "serviceId9", List.of(), List.of(new ClockInAlert(newClockInTwoId, ALERT_ONE_ID), new ClockInAlert(newClockInTwoId, ALERT_TWO_ID)));

        Mockito
            .when(idGenerator.generateId())
            .thenReturn(newClockInOneId, newClockInTwoId);

        Assertions
            .assertThat(repository.create(List.of(createClockInOne, createClockInTwo)))
            .isEqualTo(7);

        Assertions
            .assertThat(repository.find())
            .containsExactlyInAnyOrder(
                CLOCK_IN_ONE,
                CLOCK_IN_TWO,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR,
                CLOCK_IN_FIVE,
                expectedClockInOne,
                expectedClockInTwo
            );

        assertDate(expectedClockInOne);
        assertDate(expectedClockInTwo);
    }

    @Test public void
    execute_create_clock_ins_in_a_transaction() {
        UUID newClockInOneId = UUID.randomUUID();

        CreateClockInAlert createAlertOneOne = Mockito.mock(CreateClockInAlert.class);
        CreateClockIn createClockInOne = new CreateClockIn("businessId2", "employeeId3", "serviceId3", List.of(), List.of(createAlertOneOne));

        Mockito
            .when(idGenerator.generateId())
            .thenReturn(newClockInOneId);

        Mockito
            .when(createAlertOneOne.alertId())
            .thenThrow(new RuntimeException("Some Error"));

        Assertions
            .assertThatThrownBy(() -> repository.create(List.of(createClockInOne)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Some Error");

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
    update_clock_in_records() {
        CreateClockInRecord updateRecordOneOne = new CreateClockInRecord(12_500L, TimeRecordType.IN, TimeRecordAction.WORK);
        CreateClockInRecord updateRecordOneTwo = new CreateClockInRecord(15_500L, TimeRecordType.OUT, TimeRecordAction.WORK);
        CreateClockInAlert updateAlertOneOne = new CreateClockInAlert(ALERT_THREE_ID);
        CreateClockInAlert updateAlertTwoOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert updateAlertTwoTwo = new CreateClockInAlert(ALERT_TWO_ID);

        UpdateClockIn updateClockInOne = new UpdateClockIn(CLOCK_IN_ONE_ID, List.of(updateRecordOneOne, updateRecordOneTwo), List.of(updateAlertOneOne));
        UpdateClockIn updateClockInTwo = new UpdateClockIn(CLOCK_IN_TWO_ID, List.of(), List.of(updateAlertTwoOne, updateAlertTwoTwo));

        List<ClockInRecord> expectedClockInOneRecords = new LinkedList<>();
        expectedClockInOneRecords.add(new ClockInRecord(CLOCK_IN_ONE_ID, 12_500L, TimeRecordType.IN, TimeRecordAction.WORK));
        expectedClockInOneRecords.add(new ClockInRecord(CLOCK_IN_ONE_ID, 15_500L, TimeRecordType.OUT, TimeRecordAction.WORK));

        List<ClockInAlert> expectedClockInOneAlerts = new LinkedList<>();
        expectedClockInOneAlerts.add(new ClockInAlert(CLOCK_IN_ONE_ID, ALERT_THREE_ID));

        List<ClockInAlert> expectedClockInTwoAlerts = new LinkedList<>();
        expectedClockInTwoAlerts.add(new ClockInAlert(CLOCK_IN_TWO_ID, ALERT_ONE_ID));
        expectedClockInTwoAlerts.add(new ClockInAlert(CLOCK_IN_TWO_ID, ALERT_TWO_ID));

        ClockIn expectedClockInOne = new ClockIn(CLOCK_IN_ONE_ID, CLOCK_IN_ONE.businessId(), CLOCK_IN_ONE.employeeId(), CLOCK_IN_ONE.serviceId(), expectedClockInOneRecords, expectedClockInOneAlerts);
        ClockIn expectedClockInTwo = new ClockIn(CLOCK_IN_TWO_ID, CLOCK_IN_TWO.businessId(), CLOCK_IN_TWO.employeeId(), CLOCK_IN_TWO.serviceId(), List.of(), expectedClockInTwoAlerts);

        Assertions
            .assertThat(repository.update(List.of(updateClockInOne, updateClockInTwo)))
            .isEqualTo(5);

        Assertions
            .assertThat(repository.find())
            .containsExactlyInAnyOrder(
                expectedClockInOne,
                expectedClockInTwo,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR,
                CLOCK_IN_FIVE
            );

        assertDate(expectedClockInOne);
        assertDate(expectedClockInTwo);
    }

    @Test public void
    update_clock_in_records_in_a_transaction() {
        CreateClockInRecord updateRecordOneOne = new CreateClockInRecord(12_500L, TimeRecordType.IN, TimeRecordAction.WORK);
        CreateClockInAlert updateAlertOneOne = Mockito.mock(CreateClockInAlert.class);
        UpdateClockIn updateClockInOne = new UpdateClockIn(CLOCK_IN_ONE_ID, List.of(updateRecordOneOne), List.of(updateAlertOneOne));

        Mockito
            .when(updateAlertOneOne.alertId())
            .thenThrow(new RuntimeException("Some Error"));

        Assertions
            .assertThatThrownBy(() -> repository.update(List.of(updateClockInOne)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Some Error");

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
    create_and_update_clock_ins() {
        UUID newClockInTwoId = UUID.randomUUID();

        CreateClockInAlert createAlertOneOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert createAlertOneTwo = new CreateClockInAlert(ALERT_TWO_ID);

        CreateClockIn createClockInOne = new CreateClockIn("businessId1", "employeeId4", "serviceId9", List.of(), List.of(createAlertOneOne, createAlertOneTwo));
        ClockIn expectedCreatedClockInOne = new ClockIn(newClockInTwoId, "businessId1", "employeeId4", "serviceId9", List.of(), List.of(new ClockInAlert(newClockInTwoId, ALERT_ONE_ID), new ClockInAlert(newClockInTwoId, ALERT_TWO_ID)));

        CreateClockInAlert updateAlertTwoOne = new CreateClockInAlert(ALERT_ONE_ID);
        CreateClockInAlert updateAlertTwoTwo = new CreateClockInAlert(ALERT_TWO_ID);
        UpdateClockIn updateClockInTwo = new UpdateClockIn(CLOCK_IN_TWO_ID, List.of(), List.of(updateAlertTwoOne, updateAlertTwoTwo));

        List<ClockInAlert> expectedClockInTwoAlerts = new LinkedList<>();
        expectedClockInTwoAlerts.add(new ClockInAlert(CLOCK_IN_TWO_ID, ALERT_ONE_ID));
        expectedClockInTwoAlerts.add(new ClockInAlert(CLOCK_IN_TWO_ID, ALERT_TWO_ID));
        ClockIn expectedUpdatedClockInTwo = new ClockIn(CLOCK_IN_TWO_ID, CLOCK_IN_TWO.businessId(), CLOCK_IN_TWO.employeeId(), CLOCK_IN_TWO.serviceId(), List.of(), expectedClockInTwoAlerts);

        Mockito
            .when(idGenerator.generateId())
            .thenReturn(newClockInTwoId);

        Assertions
            .assertThat(repository.createAndUpdate(List.of(createClockInOne), List.of(updateClockInTwo)))
            .isEqualTo(5);

        Assertions
            .assertThat(repository.find())
            .containsExactlyInAnyOrder(
                CLOCK_IN_ONE,
                expectedUpdatedClockInTwo,
                CLOCK_IN_THREE,
                CLOCK_IN_FOUR,
                CLOCK_IN_FIVE,
                expectedCreatedClockInOne
            );
    }

    @Test public void
    execute_create_and_update_clock_ins_in_a_transaction() {
        CreateClockIn createClockIn = new CreateClockIn("businessId1", "employeeId4", "serviceId9", List.of(), List.of());
        UpdateClockIn updateClockIn = Mockito.mock(UpdateClockIn.class);

        Mockito
            .when(updateClockIn.id())
            .thenThrow(new RuntimeException("Some Error"));

        Mockito
            .when(idGenerator.generateId())
            .thenReturn(UUID.randomUUID());

        Assertions
            .assertThatThrownBy(() -> repository.createAndUpdate(List.of(createClockIn), List.of(updateClockIn)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Some Error");

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

    private void assertDate(ClockIn clockIn) {
        Long databaseDate =
            jdbcTemplate
                .queryForObject(
                    "SELECT date FROM clock_in WHERE id = :id",
                    new MapSqlParameterSource("id", clockIn.id()),
                    (resulSet, rowNum) -> (Long) resulSet.getObject(1));

        Assertions
            .assertThat(databaseDate)
            .isEqualTo(clockIn.date().orElse(null));
    }

    private DataSource initDataSource(String schema) throws SQLException {
        return TestUtils.initDatabase(schema, "repository/clock_in/initial_data.sql");
    }

    private String nextSchema() {
        return JdbcClockInRepositoryShould.class.getSimpleName() + "_" + ID_COUNTER.getAndIncrement();
    }
}