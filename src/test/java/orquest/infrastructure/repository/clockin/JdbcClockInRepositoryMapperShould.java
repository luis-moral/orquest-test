package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        ClockIn expected =
            new ClockIn(
                5L,
                "A",
                "B",
                "C",
                List.of(),
                List.of()
            );

        Mockito.when(resultSet.getLong(1)).thenReturn(5L);
        Mockito.when(resultSet.getString(2)).thenReturn("A");
        Mockito.when(resultSet.getString(3)).thenReturn("B");
        Mockito.when(resultSet.getString(4)).thenReturn("C");

        Assertions
            .assertThat(mapper.toClockIn(resultSet, 1))
            .isEqualTo(expected);
    }

    @Test public void
    map_result_set_to_clock_in_record() throws SQLException {
        ClockInRecord expected =
            new ClockInRecord(
                5L,
                10L,
                1_500L,
                ClockInRecordType.IN,
                ClockInRecordAction.WORK
            );

        Mockito.when(resultSet.getLong(1)).thenReturn(5L);
        Mockito.when(resultSet.getLong(2)).thenReturn(10L);
        Mockito.when(resultSet.getLong(3)).thenReturn(1_500L);
        Mockito.when(resultSet.getString(4)).thenReturn("IN");
        Mockito.when(resultSet.getString(5)).thenReturn("WORK");

        Assertions
            .assertThat(mapper.toClockInRecord(resultSet, 1))
            .isEqualTo(expected);
    }

    @Test public void
    map_result_set_to_clock_in_alert() throws SQLException {
        ClockInAlert expected =
            new ClockInAlert(
                5L,
                10L,
                20L
            );

        Mockito.when(resultSet.getLong(1)).thenReturn(5L);
        Mockito.when(resultSet.getLong(2)).thenReturn(10L);
        Mockito.when(resultSet.getLong(3)).thenReturn(20L);

        Assertions
            .assertThat(mapper.toClockInAlert(resultSet, 1))
            .isEqualTo(expected);
    }

    @Test public void
    add_records_and_alerts_to_clock_ins() {
        ClockIn clockInOne = new ClockIn(1L, "A1", "B1", "C1");
        ClockIn clockInTwo = new ClockIn(2L, "A2", "B2", "C2");
        ClockIn clockInThree = new ClockIn(3L, "A3", "B3", "C3");

        ClockInRecord recordOne =
            new ClockInRecord(
                1L,
                1L,
                1_500L,
                ClockInRecordType.IN,
                ClockInRecordAction.WORK
            );
        ClockInRecord recordTwo =
            new ClockInRecord(
                2L,
                1L,
                2_500L,
                ClockInRecordType.OUT,
                ClockInRecordAction.WORK
            );
        ClockInRecord recordThree =
            new ClockInRecord(
                3L,
                2L,
                3_500L,
                ClockInRecordType.OUT,
                ClockInRecordAction.REST
            );

        ClockInAlert alertOne = new ClockInAlert(1L, 1L, 1L);
        ClockInAlert alertTwo = new ClockInAlert(2L, 3L, 2L);

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