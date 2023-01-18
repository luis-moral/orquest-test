package orquest.domain.clockin.record;

public record CreateClockInRecord(
    long clockInId,
    long date,
    ClockInRecordType type,
    ClockInRecordAction action
) {}
