package orquest.domain.clockin.record;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import orquest.domain.time.TimeRecord;
import orquest.domain.time.TimeRecordType;

import java.util.UUID;

@EqualsAndHashCode
@ToString
public class ClockInRecord implements TimeRecord {

    private final UUID clockInId;
    private final long date;
    private final TimeRecordType type;
    private final ClockInRecordAction action;

    public ClockInRecord(
        UUID clockInId,
        long date,
        TimeRecordType type,
        ClockInRecordAction action
    ) {
        this.clockInId = clockInId;
        this.date = date;
        this.type = type;
        this.action = action;
    }

    public UUID clockInId() {
        return clockInId;
    }

    public long date() {
        return date;
    }

    public TimeRecordType type() {
        return type;
    }

    public ClockInRecordAction action() {
        return action;
    }
}
