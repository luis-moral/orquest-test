package orquest.domain.clockin.record;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import orquest.domain.time.TimeRecord;
import orquest.domain.time.TimeRecordType;

@EqualsAndHashCode
@ToString
public class ClockInRecord implements TimeRecord {

    private final long id;
    private final long clockInId;
    private final long date;
    private final TimeRecordType type;
    private final ClockInRecordAction action;

    public ClockInRecord(
        long id,
        long clockInId,
        long date,
        TimeRecordType type,
        ClockInRecordAction action
    ) {
        this.id = id;
        this.clockInId = clockInId;
        this.date = date;
        this.type = type;
        this.action = action;
    }

    public long id() {
        return id;
    }

    public long clockInId() {
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
