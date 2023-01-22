package orquest.infrastructure.handler.clockin_employee.get;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import orquest.domain.clockin_employee.GetEmployeeClockInService;
import reactor.core.publisher.Mono;

public class GetEmployeeClockInHandler {

    private final GetEmployeeClockInService getEmployeeClockInService;
    private final GetEmployeeClockInHandlerMapper mapper;

    public GetEmployeeClockInHandler(
        GetEmployeeClockInService getEmployeeClockInService,
        GetEmployeeClockInHandlerMapper mapper
    ) {
        this.getEmployeeClockInService = getEmployeeClockInService;
        this.mapper = mapper;
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return
            ServerResponse
                .status(HttpStatus.OK)
                .body(
                    getEmployeeClockInService
                        .getByWeek(mapper.toEmployeeId(request.pathVariable("employee_id")))
                        .map(mapper::toGetEmployeeClockInResponse),
                    GetEmployeeClockInResponse.class
                );
    }
}
