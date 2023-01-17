package orquest.domain.clockin;

public record CreateEmployeeClockIn(
    String businessId,
    long date,
    String employeeId,
    ClockInRecordType recordType,
    String serviceId,
    ClockInType type
) {}
