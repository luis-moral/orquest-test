package orquest.domain.clockin;

public record ImportedClockIn(
    String businessId,
    long date,
    String employeeId,
    ClockInRecordType recordType,
    String serviceId,
    ClockInType type
) {}
