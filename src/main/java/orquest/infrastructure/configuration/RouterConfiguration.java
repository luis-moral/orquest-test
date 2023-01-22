package orquest.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import orquest.infrastructure.handler.clockin.post.PostClockInHandler;
import orquest.infrastructure.handler.clockin_employee.get.GetEmployeeClockInHandler;
import orquest.infrastructure.handler.status.get.GetStatusHandler;

@Configuration
public class RouterConfiguration {

    @Value("${endpoint.status.path}")
    private String statusEndpoint;

    @Value("${endpoint.v1.clockin.path.base}")
    private String clockInEndpoint;

    @Value("${endpoint.v1.clockin.path.by-employee-id}")
    private String clockInEndpointByEmployee;

    @Bean
    public RouterFunction<ServerResponse> routes(
        GetStatusHandler getStatusHandler,
        PostClockInHandler postClockInHandler,
        GetEmployeeClockInHandler getEmployeeClockInHandler
    ) {
        return
            RouterFunctions
                .route(RequestPredicates.GET(statusEndpoint), getStatusHandler::status)
                .andRoute(RequestPredicates.POST(clockInEndpoint), postClockInHandler::process)
                .andRoute(RequestPredicates.GET(clockInEndpointByEmployee), getEmployeeClockInHandler::get);
    }
}
