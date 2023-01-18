package orquest.domain.clockin.record;

public class ClockInRecord {

    private final long id;
    private final long clockInId;
    private final long date;
    private final ClockInRecordType type;
    private final ClockInRecordAction action;

    public ClockInRecord(
        long id,
        long clockInId,
        long date,
        ClockInRecordType type,
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

    public ClockInRecordType type() {
        return type;
    }

    public ClockInRecordAction action() {
        return action;
    }
}
