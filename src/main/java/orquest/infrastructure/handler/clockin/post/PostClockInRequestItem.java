package orquest.infrastructure.handler.clockin.post;

public record PostClockInRequestItem(
    String businessId,
    String date,
    String employeeId,
    String recordType,
    String serviceId,
    String type
) {}