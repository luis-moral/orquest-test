package orquest.domain.time;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class TimeUtils {

    private TimeUtils() {
    }

    public static Optional<Long> clockInDay(List<? extends TimeRecord> dates) {
        return
            dates
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
}
