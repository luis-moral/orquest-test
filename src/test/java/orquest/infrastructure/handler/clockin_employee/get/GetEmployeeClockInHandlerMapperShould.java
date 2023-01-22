package orquest.infrastructure.handler.clockin_employee.get;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.infrastructure.util.validator.RequestParameterValidator;

import java.util.Optional;

public class GetEmployeeClockInHandlerMapperShould {

    private RequestParameterValidator parameterValidator;
    private GetEmployeeClockInHandlerMapper mapper;

    @BeforeEach
    public void setUp() {
        parameterValidator = Mockito.mock(RequestParameterValidator.class);

        mapper = new GetEmployeeClockInHandlerMapper(parameterValidator);
    }

    @Test public void
    map_server_request_to_employee_id() {
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("employeeId1"), "employeeId"))
            .thenReturn("employeeId1");

        Assertions
            .assertThat(mapper.toEmployeeId("employeeId1"))
            .isEqualTo("employeeId1");

        Mockito
            .verify(parameterValidator, Mockito.times(1))
            .mandatoryString(Mockito.any(), Mockito.any());
    }
}