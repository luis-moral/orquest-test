package orquest.infrastructure.handler.clockin_employee.get;

import org.springframework.web.reactive.function.server.ServerRequest;
import orquest.domain.clockin_employee.ClockInsByWeek;
import orquest.infrastructure.util.validator.RequestParameterValidator;

import java.util.List;

public class GetEmployeeClockInHandlerMapper {

    private final RequestParameterValidator requestParameterValidator;

    public GetEmployeeClockInHandlerMapper(RequestParameterValidator requestParameterValidator) {
        this.requestParameterValidator = requestParameterValidator;
    }

    public long toEmployeeId(ServerRequest serverRequest) {
        throw new UnsupportedOperationException();
    }

    public GetEmployeeClockInResponse toGetEmployeeClockInResponse(List<ClockInsByWeek> clockInsByWeeks) {
        throw new UnsupportedOperationException();
    }
}
