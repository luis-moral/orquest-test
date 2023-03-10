package orquest.domain.clockin;

import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.CreateClockInRecord;
import orquest.domain.time.TimeRecordGroup;

import java.util.List;
import java.util.UUID;

public record UpdateClockIn(
    UUID id,
    List<CreateClockInRecord> records,
    List<CreateClockInAlert> alerts
) implements TimeRecordGroup {}
