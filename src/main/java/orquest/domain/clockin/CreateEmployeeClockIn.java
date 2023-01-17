package orquest.domain.clockin;

import java.time.ZonedDateTime;

public record CreateEmployeeClockIn(
    String businessId,
    ZonedDateTime date,
    String employeeId,
    ClockInActionType action,
    String serviceId,
    ClockInRecordType type
) {}
