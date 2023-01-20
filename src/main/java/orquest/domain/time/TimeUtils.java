package orquest.domain.time;

import java.time.Instant;
import java.time.ZoneOffset;
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

    public static long timeDifference(List<? extends TimeRecord> records) {
        LongSummaryStatistics statistics =
            records
                .stream()
                .mapToLong(TimeRecord::date)
                .summaryStatistics();

        return statistics.getMax() - statistics.getMin();
    }
}
