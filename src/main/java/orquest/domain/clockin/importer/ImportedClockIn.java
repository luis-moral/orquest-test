package orquest.domain.clockin.importer;

import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;

import java.time.ZonedDateTime;

public record ImportedClockIn(
    String businessId,
    ZonedDateTime date,
    String employeeId,
    ClockInRecordType type,
    String serviceId,
    ClockInRecordAction action
) {
}
