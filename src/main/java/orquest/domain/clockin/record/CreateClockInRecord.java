package orquest.domain.clockin.record;

import orquest.domain.time.TimeRecord;

public record CreateClockInRecord(
    long date,
    ClockInRecordType type,
    ClockInRecordAction action
) implements TimeRecord {}
