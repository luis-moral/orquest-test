package orquest.domain.clockin;

import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.CreateClockInRecord;
import orquest.domain.time.TimeUtils;

import java.util.List;
import java.util.Optional;

public record UpdateClockIn(
    long id,
    List<CreateClockInRecord> records,
    List<CreateClockInAlert> alerts
) {
    public Optional<Long> date() {
        return TimeUtils.clockInDay(records);
    }

    public long timeWorked() {
        return TimeUtils.timeDifference(records);
    }
}
