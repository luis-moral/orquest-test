package orquest.domain.clockin.record;

import orquest.domain.time.TimeRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;

public record CreateClockInRecord(
    long date,
    TimeRecordType type,
    TimeRecordAction action
) implements TimeRecord {}
