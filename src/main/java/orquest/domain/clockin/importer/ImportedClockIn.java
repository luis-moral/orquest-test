package orquest.domain.clockin.importer;

import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.time.TimeRecordType;

import java.time.ZonedDateTime;

public record ImportedClockIn(
    String businessId,
    ZonedDateTime date,
    String employeeId,
    TimeRecordType type,
    String serviceId,
    ClockInRecordAction action
) {
}
