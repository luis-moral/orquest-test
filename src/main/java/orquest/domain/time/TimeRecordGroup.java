package orquest.domain.time;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface TimeRecordGroup {

    List<? extends TimeRecord> records();

    default Optional<Long> date() {
        return TimeRecordUtils.clockInDay(records());
    }

    default Optional<DayOfWeek> dayOfWeek() {
        return TimeRecordUtils.clockInDayOfTheWeek(records());
    }

    default long timeWorked() {
        return TimeRecordUtils.timeDifference(records());
    }

    default int firstRecordHourOfDay() {
        return TimeRecordUtils.firstRecordHourOfDay(records());
    }

    default boolean hasMatchedRecords() {
        return TimeRecordUtils.hasMatchedRecords(records());
    }
}
