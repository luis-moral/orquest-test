package orquest.infrastructure.handler.clockin_employee.get;

import orquest.domain.clockin_employee.ClockInsByWeek;
import orquest.infrastructure.util.validator.RequestParameterValidator;

import java.util.Optional;

public class GetEmployeeClockInHandlerMapper {

    private final RequestParameterValidator parameterValidator;

    public GetEmployeeClockInHandlerMapper(RequestParameterValidator parameterValidator) {
        this.parameterValidator = parameterValidator;
    }

    public String toEmployeeId(String employeeId) {
        return parameterValidator.mandatoryString(Optional.ofNullable(employeeId), "employeeId");
    }

    public GetEmployeeClockInResponse toGetEmployeeClockInResponse(ClockInsByWeek clockInsByWeeks) {
        throw new UnsupportedOperationException();
    }
}
