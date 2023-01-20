package orquest.domain.time;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Optional;

public class TimeUtils {

    private TimeUtils() {
    }

    public static Optional<Long> clockInDay(List<? extends TimeRecord> records) {
        return
            records
                .stream()
                .findAny()
                .map(
                    date ->
                        Instant
                            .ofEpochMilli(date.date())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli()
                );
    }

    public static Optional<DayOfWeek> clockInDayOfTheWeek(List<? extends TimeRecord> records) {
        return
            records
                .stream()
                .findAny()
                .map(
                    date ->
                        Instant
                            .ofEpochMilli(date.date())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .getDayOfWeek()
                );
    }

    public static long timeDifference(List<? extends TimeRecord> records) {
        LongSummaryStatistics statistics =
            records
                .stream()
                .mapToLong(TimeRecord::date)
                .summaryStatistics();

        return statistics.getMax() - statistics.getMin();
    }

    public static int firstRecordHourOfDay(List<? extends TimeRecord> records) {
        long minimumDate =
            records
                .stream()
                .mapToLong(TimeRecord::date)
                .summaryStatistics()
                .getMin();

        return
            ZonedDateTime
                .ofInstant(Instant.ofEpochMilli(minimumDate), ZoneOffset.UTC)
                .toLocalDateTime()
                .getHour();
    }
}
