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
        TimeRecord timeRecordOne = new TestTimeRecord(TimeUnit.DAYS.toMillis(1), TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordTwo = new TestTimeRecord(TimeUnit.DAYS.toMillis(1) + 10_500L, TimeRecordType.IN, TimeRecordAction.WORK);

        Assertions
            .assertThat(TimeRecordUtils.clockInDay(List.of(timeRecordOne, timeRecordTwo)))
            .hasValue(TimeUnit.DAYS.toMillis(1));
    }

    @Test public void
    calculate_clock_in_day_of_the_week() {
        TimeRecord timeRecordOne = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T15:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordTwo = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T16:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN, TimeRecordAction.WORK);

        Assertions
            .assertThat(TimeRecordUtils.clockInDayOfTheWeek(List.of(timeRecordOne, timeRecordTwo)))
            .hasValue(DayOfWeek.FRIDAY);
    }

    @Test public void
    calculate_time_worked() {
        long firstTimeIn = TimeUnit.DAYS.toMillis(1) + TimeUnit.HOURS.toMillis(8) ;
        long firstTimeOut = firstTimeIn + TimeUnit.HOURS.toMillis(6);

        long restTimeIn = firstTimeIn + TimeUnit.HOURS.toMillis(2);
        long restTimeOut = restTimeIn + TimeUnit.HOURS.toMillis(1);

        long secondTimeIn = firstTimeOut + TimeUnit.HOURS.toMillis(3);
        long secondTimeOut = secondTimeIn + TimeUnit.HOURS.toMillis(3);

        List<TestTimeRecord> records =
            List
                .of(
                    new TestTimeRecord(firstTimeIn, TimeRecordType.IN, TimeRecordAction.WORK),
                    new TestTimeRecord(firstTimeOut, TimeRecordType.OUT, TimeRecordAction.WORK),
                    new TestTimeRecord(restTimeIn, TimeRecordType.IN, TimeRecordAction.REST),
                    new TestTimeRecord(restTimeOut, TimeRecordType.OUT, TimeRecordAction.REST),
                    new TestTimeRecord(secondTimeIn, TimeRecordType.IN, TimeRecordAction.WORK),
                    new TestTimeRecord(secondTimeOut, TimeRecordType.OUT, TimeRecordAction.WORK)
                );

        Assertions
            .assertThat(TimeRecordUtils.timeWorked(records))
            .isEqualTo(TimeUnit.HOURS.toMillis(8));
    }

    @Test public void
    calculate_first_record_hour_of_day() {
        TimeRecord timeRecordOne = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T16:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordTwo = new TestTimeRecord(ZonedDateTime.parse("2023-01-20T15:52:19.000Z").toInstant().toEpochMilli(), TimeRecordType.IN, TimeRecordAction.WORK);

        Assertions
            .assertThat(TimeRecordUtils.firstRecordHourOfDay(List.of(timeRecordOne, timeRecordTwo)))
            .isEqualTo(15);
    }

    @Test public void
    match_records() {
        TimeRecord timeRecordOneIn = new TestTimeRecord(10L, TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordTwoIn = new TestTimeRecord(15L, TimeRecordType.IN, TimeRecordAction.REST);
        TimeRecord timeRecordTwoOut = new TestTimeRecord(16L, TimeRecordType.OUT, TimeRecordAction.REST);
        TimeRecord timeRecordOneOut = new TestTimeRecord(20L, TimeRecordType.OUT, TimeRecordAction.WORK);
        TimeRecord timeRecordThreeIn = new TestTimeRecord(30L, TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordThreeOut = new TestTimeRecord(31L, TimeRecordType.OUT, TimeRecordAction.WORK);

        Assertions
            .assertThat(
                TimeRecordUtils
                    .matchRecords(
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoIn, timeRecordTwoOut, timeRecordThreeIn, timeRecordThreeOut)
                    )
            )
            .containsExactlyInAnyOrder(
                new MatchedTimeRecord(timeRecordOneIn, timeRecordOneOut),
                new MatchedTimeRecord(timeRecordTwoIn, timeRecordTwoOut),
                new MatchedTimeRecord(timeRecordThreeIn, timeRecordThreeOut)
            );

        Assertions
            .assertThat(
                TimeRecordUtils
                    .matchRecords(
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoIn, timeRecordThreeIn, timeRecordThreeOut)
                    )
            )
            .containsExactlyInAnyOrder(
                new MatchedTimeRecord(timeRecordOneIn, timeRecordOneOut),
                new MatchedTimeRecord(timeRecordTwoIn, null),
                new MatchedTimeRecord(timeRecordThreeIn, timeRecordThreeOut)
            );

        Assertions
            .assertThat(
                TimeRecordUtils
                    .matchRecords(
                        List.of(timeRecordOneOut, timeRecordTwoIn, timeRecordThreeOut)
                    )
            )
            .containsExactlyInAnyOrder(
                new MatchedTimeRecord(null, timeRecordOneOut),
                new MatchedTimeRecord(timeRecordTwoIn, null),
                new MatchedTimeRecord(null, timeRecordThreeOut)
            );
    }
    
    @Test public void 
    return_if_has_matched_records() {
        TimeRecord timeRecordOneIn = new TestTimeRecord(10L, TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordOneOut = new TestTimeRecord(11L, TimeRecordType.OUT, TimeRecordAction.WORK);
        TimeRecord timeRecordTwoIn = new TestTimeRecord(20L, TimeRecordType.IN, TimeRecordAction.REST);
        TimeRecord timeRecordTwoOut = new TestTimeRecord(21L, TimeRecordType.OUT, TimeRecordAction.REST);
        TimeRecord timeRecordThreeIn = new TestTimeRecord(30L, TimeRecordType.IN, TimeRecordAction.WORK);
        TimeRecord timeRecordThreeOut = new TestTimeRecord(31L, TimeRecordType.OUT, TimeRecordAction.WORK);

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
                        List.of(timeRecordOneIn, timeRecordOneOut, timeRecordTwoIn, timeRecordThreeOut)
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

    private record TestTimeRecord(long date, TimeRecordType type, TimeRecordAction action) implements TimeRecord {}
}