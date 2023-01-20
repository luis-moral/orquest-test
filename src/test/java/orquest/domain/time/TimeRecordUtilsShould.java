package orquest.domain.time;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeRecordUtilsShould {

    @Test public void
    calculate_clock_in_day() {
        TimeRecord timeRecordOne = new TestTimeRecord(TimeUnit.DAYS.toMillis(1), TimeRecordType.IN);
        TimeRecord timeRecordTwo = new TestTimeRecord(TimeUnit.DAYS.toMillis(1) + 10_500L, TimeRecordType.IN);

        Assertions
            .assertThat(TimeRecordUtils.clockInDay(List.of(timeRecordOne, timeRecordTwo)))
            .hasValue(TimeUnit.DAYS.toMillis(1));
    }

    @Test public void
    calculate_clock_in_day_of_the_week() {
        TimeRecord timeRecordOne = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T15:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN);
        TimeRecord timeRecordTwo = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T16:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN);

        Assertions
            .assertThat(TimeRecordUtils.clockInDayOfTheWeek(List.of(timeRecordOne, timeRecordTwo)))
            .hasValue(DayOfWeek.FRIDAY);
    }

    @Test public void
    calculate_time_difference() {
        TimeRecord timeRecordOne = new TestTimeRecord(TimeUnit.DAYS.toMillis(1), TimeRecordType.IN);
        TimeRecord timeRecordTwo = new TestTimeRecord(TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(5) + 30_000L, TimeRecordType.IN);
        TimeRecord timeRecordThree = new TestTimeRecord(TimeUnit.DAYS.toMillis(1) + 50_000L, TimeRecordType.IN);

        Assertions
            .assertThat(TimeRecordUtils.timeDifference(List.of(timeRecordOne, timeRecordTwo, timeRecordThree)))
            .isEqualTo(TimeUnit.HOURS.toMillis(5) + 30_000L);
    }

    @Test public void
    calculate_first_record_hour_of_day() {
        TimeRecord timeRecordOne = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T16:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN);
        TimeRecord timeRecordTwo = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T15:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN);

        Assertions
            .assertThat(TimeRecordUtils.firstRecordHourOfDay(List.of(timeRecordOne, timeRecordTwo)))
            .isEqualTo(15);
    }
    
    @Test public void 
    calculate_if_has_matched_records() {
        TimeRecord timeRecordOneIn = new TestTimeRecord(10L, TimeRecordType.IN);
        TimeRecord timeRecordOneOut = new TestTimeRecord(11L, TimeRecordType.OUT);
        TimeRecord timeRecordTwoIn = new TestTimeRecord(20L, TimeRecordType.IN);
        TimeRecord timeRecordTwoOut = new TestTimeRecord(21L, TimeRecordType.OUT);
        TimeRecord timeRecordThreeIn = new TestTimeRecord(30L, TimeRecordType.IN);
        TimeRecord timeRecordThreeOut = new TestTimeRecord(31L, TimeRecordType.OUT);

        Assertions
            .assertThat(
                TimeRecordUtils
                    .hasMatchedRecords(
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoIn, timeRecordTwoOut, timeRecordThreeIn, timeRecordThreeOut)
                    )
            )
            .isTrue();

        Assertions
            .assertThat(
                TimeRecordUtils
                    .hasMatchedRecords(
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoIn)
                    )
            )
            .isFalse();

        Assertions
            .assertThat(
                TimeRecordUtils
                    .hasMatchedRecords(
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoOut, timeRecordThreeIn)
                    )
            )
            .isFalse();

        Assertions
            .assertThat(
                TimeRecordUtils
                    .hasMatchedRecords(
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoIn, timeRecordThreeIn)
                    )
            )
            .isFalse();
    }

    private record TestTimeRecord(long date, TimeRecordType type) implements TimeRecord {}
}