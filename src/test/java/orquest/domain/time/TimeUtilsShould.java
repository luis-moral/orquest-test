package orquest.domain.time;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtilsShould {

    @Test public void
    calculate_clock_in_day() {
        TimeRecord timeRecordOne = () -> TimeUnit.DAYS.toMillis(1);
        TimeRecord timeRecordTwo = () -> TimeUnit.DAYS.toMillis(1) + 10_500L;

        Assertions
            .assertThat(TimeUtils.clockInDay(List.of(timeRecordOne, timeRecordTwo)))
            .hasValue(TimeUnit.DAYS.toMillis(1));
    }

    @Test public void
    calculate_time_difference() {
        TimeRecord timeRecordOne = () -> TimeUnit.DAYS.toMillis(1);
        TimeRecord timeRecordTwo = () -> TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(5) + 30_000L;
        TimeRecord timeRecordThree = () -> TimeUnit.DAYS.toMillis(1) + 50_000L;

        Assertions
            .assertThat(TimeUtils.timeDifference(List.of(timeRecordOne, timeRecordTwo, timeRecordThree)))
            .isEqualTo(TimeUnit.HOURS.toMillis(5) + 30_000L);
    }
}