package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class JdbcClockInRepositoryMapperShould {

    private ResultSet resultSet;
    private JdbcClockInRepositoryMapper mapper;
    
    @BeforeEach
    public void setUp() {
        resultSet = Mockito.mock(ResultSet.class);

        mapper = new JdbcClockInRepositoryMapper();
    }

    @Test public void
    map_result_set_to_clock_in() throws SQLException {
        UUID clockInId = UUID.randomUUID();

        ClockIn expected =
            new ClockIn(
                clockInId,
                "A",
                "B",
                "C",
                List.of(),
                List.of()
            );

        Mockito.when(resultSet.getObject(1, UUID.class)).thenReturn(clockInId);
        Mockito.when(resultSet.getString(2)).thenReturn("A");
        Mockito.when(resultSet.getString(3)).thenReturn("B");
        Mockito.when(resultSet.getString(4)).thenReturn("C");

        Assertions
            .assertThat(mapper.toClockIn(resultSet, 1))
            .isEqualTo(expected);
    }

    @Test public void
    map_result_set_to_clock_in_record() throws SQLException {
        UUID clockInId = UUID.randomUUID();

        ClockInRecord expected =
            new ClockInRecord(
                clockInId,
                1_500L,
                TimeRecordType.IN,
                TimeRecordAction.WORK
            );

        Mockito.when(resultSet.getObject(1, UUID.class)).thenReturn(clockInId);
        Mockito.when(resultSet.getLong(2)).thenReturn(1_500L);
        Mockito.when(resultSet.getString(3)).thenReturn("IN");
        Mockito.when(resultSet.getString(4)).thenReturn("WORK");

        Assertions
            .assertThat(mapper.toClockInRecord(resultSet, 1))
            .isEqualTo(expected);
    }

    @Test public void
    map_result_set_to_clock_in_alert() throws SQLException {
        UUID clockInId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        ClockInAlert expected =
            new ClockInAlert(
                clockInId,
                alertId
            );

        Mockito.when(resultSet.getObject(1, UUID.class)).thenReturn(clockInId);
        Mockito.when(resultSet.getObject(2, UUID.class)).thenReturn(alertId);

        Assertions
            .assertThat(mapper.toClockInAlert(resultSet, 1))
            .isEqualTo(expected);
    }

    @Test public void
    add_records_and_alerts_to_clock_ins() {
        UUID clockInOneId = UUID.randomUUID();
        UUID clockInTwoId = UUID.randomUUID();
        UUID clockInThreeId = UUID.randomUUID();
        UUID alertOneId = UUID.randomUUID();
        UUID alertTwoId = UUID.randomUUID();

        ClockIn clockInOne = new ClockIn(clockInOneId, "A1", "B1", "C1");
        ClockIn clockInTwo = new ClockIn(clockInTwoId, "A2", "B2", "C2");
        ClockIn clockInThree = new ClockIn(clockInThreeId, "A3", "B3", "C3");

        ClockInRecord recordOne =
            new ClockInRecord(
                clockInOneId,
                1_500L,
                TimeRecordType.IN,
                TimeRecordAction.WORK
            );
        ClockInRecord recordTwo =
            new ClockInRecord(
                clockInOneId,
                2_500L,
                TimeRecordType.OUT,
                TimeRecordAction.WORK
            );
        ClockInRecord recordThree =
            new ClockInRecord(
                clockInTwoId,
                3_500L,
                TimeRecordType.OUT,
                TimeRecordAction.REST
            );

        ClockInAlert alertOne = new ClockInAlert(clockInOneId, alertOneId);
        ClockInAlert alertTwo = new ClockInAlert(clockInThreeId, alertTwoId);

        mapper
            .add(
                List.of(clockInOne, clockInTwo, clockInThree),
                List.of(recordOne, recordTwo, recordThree),
                List.of(alertOne, alertTwo)
            );

        Assertions
            .assertThat(clockInOne.records())
            .containsExactlyInAnyOrder(recordOne, recordTwo);
        Assertions
            .assertThat(clockInOne.alerts())
            .containsExactlyInAnyOrder(alertOne);

        Assertions
            .assertThat(clockInTwo.records())
            .containsExactlyInAnyOrder(recordThree);
        Assertions
            .assertThat(clockInTwo.alerts())
            .isEmpty();

        Assertions
            .assertThat(clockInThree.records())
            .isEmpty();
        Assertions
            .assertThat(clockInThree.alerts())
            .containsExactlyInAnyOrder(alertTwo);
    }
}