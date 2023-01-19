package orquest.domain.clockin.record;

public record CreateClockInRecord(
    long date,
    ClockInRecordType type,
    ClockInRecordAction action
) {}
