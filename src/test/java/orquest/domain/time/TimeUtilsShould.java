package orquest.domain.time;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
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
    calculate_clock_in_day_of_the_week() {
        TimeRecord timeRecordOne = () -> ZonedDateTime.parse("2023-01-20T15:52:19.000Z").toInstant().toEpochMilli();
        TimeRecord timeRecordTwo = () -> ZonedDateTime.parse("2023-01-20T16:52:19.000Z").toInstant().toEpochMilli();

        Assertions
            .assertThat(TimeUtils.clockInDayOfTheWeek(List.of(timeRecordOne, timeRecordTwo)))
            .hasValue(DayOfWeek.FRIDAY);
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

    @Test public void
    calculate_first_record_hour_of_day() {
        TimeRecord timeRecordOne = () -> ZonedDateTime.parse("2023-01-20T16:52:19.000Z").toInstant().toEpochMilli();
        TimeRecord timeRecordTwo = () -> ZonedDateTime.parse("2023-01-20T15:52:19.000Z").toInstant().toEpochMilli();

        Assertions
            .assertThat(TimeUtils.firstRecordHourOfDay(List.of(timeRecordOne, timeRecordTwo)))
            .isEqualTo(15);
    }
}