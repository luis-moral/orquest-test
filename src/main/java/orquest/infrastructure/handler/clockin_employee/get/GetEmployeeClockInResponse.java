package orquest.infrastructure.handler.clockin_employee.get;

import java.util.List;
import java.util.UUID;

public record GetEmployeeClockInResponse(
    String businessId,
    String employeeId,
    List<ClockInWeekResponse> weeks
) {

    public record ClockInWeekResponse(
        int weekOfYear,
        int year,
        long timeWorked,
        List<ClockInResponse> clockIns
    ) {}

    public record ClockInResponse(
        UUID id,
        String serviceId,
        List<ClockInRecordResponse> records,
        List<String> alerts
    ) {}

    public record ClockInRecordResponse(
        long date,
        String type,
        String action
    ) {}
}
