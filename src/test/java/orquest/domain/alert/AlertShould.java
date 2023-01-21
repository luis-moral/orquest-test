package orquest.domain.alert;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AlertShould {

    @Test public void 
    check_for_matched_records_expression() {
        UpdateClockIn updateClockIn = Mockito.mock(UpdateClockIn.class);
        CreateClockIn createClockIn = Mockito.mock(CreateClockIn.class);

        Alert alert =
            new Alert(
                UUID.randomUUID(),
                "A",
                "#clockIn.hasMatchedRecords()",
                "message"
            );

        Mockito
            .when(updateClockIn.hasMatchedRecords())
            .thenReturn(true);
        Mockito
            .when(createClockIn.hasMatchedRecords())
            .thenReturn(false);

        Assertions
            .assertThat(alert.checkFor(updateClockIn))
            .isTrue();
        Assertions
            .assertThat(alert.checkFor(createClockIn))
            .isFalse();
    }
    
    @Test public void
    check_for_maximum_hours_worked_expression() {
        UpdateClockIn updateClockIn = Mockito.mock(UpdateClockIn.class);
        CreateClockIn createClockIn = Mockito.mock(CreateClockIn.class);

        Alert alert =
            new Alert(
                UUID.randomUUID(),
                "A",
                "#clockIn.timeWorked() > T(java.util.concurrent.TimeUnit).HOURS.toMillis(10)",
                "message"
            );

        Mockito
            .when(updateClockIn.timeWorked())
            .thenReturn(TimeUnit.HOURS.toMillis(11));
        Mockito
            .when(createClockIn.timeWorked())
            .thenReturn(TimeUnit.HOURS.toMillis(5));

        Assertions
            .assertThat(alert.checkFor(updateClockIn))
            .isTrue();
        Assertions
            .assertThat(alert.checkFor(createClockIn))
            .isFalse();
    }

    @Test public void
    check_for_minimum_clock_in_time_expression() {
        UpdateClockIn mondayBefore = Mockito.mock(UpdateClockIn.class);
        UpdateClockIn mondayAfter = Mockito.mock(UpdateClockIn.class);

        CreateClockIn fridayBefore = Mockito.mock(CreateClockIn.class);
        CreateClockIn fridayAfter = Mockito.mock(CreateClockIn.class);

        Alert alert =
            new Alert(
                UUID.randomUUID(),
                "A",
                """
                (#clockIn.dayOfWeek().isPresent()) and
                    (
                        (
                            #clockIn.dayOfWeek().get() >= T(java.time.DayOfWeek).MONDAY &&
                            #clockIn.dayOfWeek().get() <= T(java.time.DayOfWeek).THURSDAY && 
                            #clockIn.firstRecordHourOfDay() >= 8
                        )
                    or
                        (
                            #clockIn.dayOfWeek().get() == T(java.time.DayOfWeek).FRIDAY && 
                            #clockIn.firstRecordHourOfDay() >= 7
                        )
                    )
                """,
                "message"
            );

        Mockito
            .when(mondayBefore.dayOfWeek())
            .thenReturn(Optional.of(DayOfWeek.MONDAY));
        Mockito
            .when(mondayBefore.firstRecordHourOfDay())
            .thenReturn(5);

        Mockito
            .when(mondayAfter.dayOfWeek())
            .thenReturn(Optional.of(DayOfWeek.MONDAY));
        Mockito
            .when(mondayAfter.firstRecordHourOfDay())
            .thenReturn(8);

        Mockito
            .when(fridayBefore.dayOfWeek())
            .thenReturn(Optional.of(DayOfWeek.FRIDAY));
        Mockito
            .when(fridayBefore.firstRecordHourOfDay())
            .thenReturn(6);

        Mockito
            .when(fridayAfter.dayOfWeek())
            .thenReturn(Optional.of(DayOfWeek.FRIDAY));
        Mockito
            .when(fridayAfter.firstRecordHourOfDay())
            .thenReturn(7);

        Assertions
            .assertThat(alert.checkFor(mondayBefore))
            .isFalse();
        Assertions
            .assertThat(alert.checkFor(mondayAfter))
            .isTrue();
        Assertions
            .assertThat(alert.checkFor(fridayBefore))
            .isFalse();
        Assertions
            .assertThat(alert.checkFor(fridayAfter))
            .isTrue();
    }
}