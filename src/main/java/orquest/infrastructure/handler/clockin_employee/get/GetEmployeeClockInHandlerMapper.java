package orquest.infrastructure.handler.clockin_employee.get;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin_employee.ClockInsByWeek;
import orquest.infrastructure.util.validator.RequestParameterValidator;

import java.util.List;
import java.util.Optional;

public class GetEmployeeClockInHandlerMapper {

    private final RequestParameterValidator parameterValidator;

    public GetEmployeeClockInHandlerMapper(RequestParameterValidator parameterValidator) {
        this.parameterValidator = parameterValidator;
    }

    public String toEmployeeId(String employeeId) {
        return parameterValidator.mandatoryString(Optional.ofNullable(employeeId), "employeeId");
    }

    public GetEmployeeClockInResponse toGetEmployeeClockInResponse(ClockInsByWeek clockInsByWeek) {
        return
            new GetEmployeeClockInResponse(
                clockInsByWeek
                    .clockInWeeks()
                    .stream()
                    .map(this::toClockInWeekResponse)
                    .toList()
            );
    }

    private GetEmployeeClockInResponse.ClockInWeekResponse toClockInWeekResponse(ClockInsByWeek.ClockInWeek clockInWeek) {
        return
            new GetEmployeeClockInResponse.ClockInWeekResponse(
                clockInWeek.weekOfYear(),
                clockInWeek.timeWorked(),
                clockInWeek.clockIns().stream().map(this::toClockInResponse).toList()
            );
    }

    private GetEmployeeClockInResponse.ClockInResponse toClockInResponse(ClockIn clockIn) {
        return
            new GetEmployeeClockInResponse.ClockInResponse(
                clockIn.id(),
                clockIn.businessId(),
                clockIn.employeeId(),
                clockIn.serviceId(),
                ToClockInRecordResponse(clockIn),
                clockIn.alerts().stream().map(alert -> alert.alertId().toString()).toList()
            );
    }

    private List<GetEmployeeClockInResponse.ClockInRecordResponse> ToClockInRecordResponse(ClockIn clockIn) {
        return
            clockIn
                .records()
                .stream()
                .map(
                    record ->
                        new GetEmployeeClockInResponse.ClockInRecordResponse(
                            record.date(),
                            record.type().toString(),
                            record.action().toString()
                        )
                )
                .toList();
    }
}
