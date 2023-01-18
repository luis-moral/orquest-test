package orquest.domain.clockin.importer;

import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;

public record ImportedClockIn(
    String businessId,
    long date,
    String employeeId,
    ClockInRecordType type,
    String serviceId,
    ClockInRecordAction action
) {}
