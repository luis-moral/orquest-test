package orquest.domain.time;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface TimeRecordGroup {

    List<? extends TimeRecord> records();

    default Optional<Long> date() {
        return TimeUtils.clockInDay(records());
    }

    default Optional<DayOfWeek> dayOfWeek() {
        return TimeUtils.clockInDayOfTheWeek(records());
    }

    default long timeWorked() {
        return TimeUtils.timeDifference(records());
    }

    default int firstRecordHourOfDay() {
        return TimeUtils.firstRecordHourOfDay(records());
    }
}
