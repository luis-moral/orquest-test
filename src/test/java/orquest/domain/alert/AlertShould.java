package orquest.domain.alert;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;

import java.util.concurrent.TimeUnit;

public class AlertShould {

    @Test public void
    check_for_maximum_hours_worked_expression() {
        UpdateClockIn updateClockIn = Mockito.mock(UpdateClockIn.class);
        CreateClockIn createClockIn = Mockito.mock(CreateClockIn.class);

        Alert alert =
            new Alert(
                1L,
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
}