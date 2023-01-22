package orquest.domain.clockin_employee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.ClockInRepository;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

public class GetEmployeeClockInServiceShould {

    private ClockInRepository clockInRepository;
    private GetEmployeeClockInServiceMapper mapper;
    private GetEmployeeClockInService service;

    @BeforeEach
    public void setUp() {
        clockInRepository = Mockito.mock(ClockInRepository.class);
        mapper = Mockito.mock(GetEmployeeClockInServiceMapper.class);

        service = new GetEmployeeClockInService(clockInRepository, mapper);
    }

    @Test public void
    return_employee_time_records_grouped_by_week() {
        ClockInFilter filter = new ClockInFilter.Builder().businessIds(Set.of("businessId1")).employeeIds(Set.of("employeeId1")).build();
        List<ClockIn> clockIns = List.of(Mockito.mock(ClockIn.class), Mockito.mock(ClockIn.class));

        ClockInsByWeek expected = Mockito.mock(ClockInsByWeek.class);

        Mockito
            .when(mapper.toFilter("businessId1", "employeeId1"))
            .thenReturn(filter);

        Mockito
            .when(clockInRepository.find(filter))
            .thenReturn(clockIns);

        Mockito
            .when(mapper.toClockInsByWeek(clockIns))
            .thenReturn(expected);

        StepVerifier
            .create(service.getByWeek("businessId1", "employeeId1"))
            .assertNext(
                clockInsByWeek ->
                    Assertions
                        .assertThat(clockInsByWeek)
                        .isEqualTo(expected)
            )
            .verifyComplete();

        Mockito
            .verify(mapper, Mockito.times(1))
            .toFilter(Mockito.any(), Mockito.any());

        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .find(Mockito.any());

        Mockito
            .verify(mapper, Mockito.times(1))
            .toClockInsByWeek(Mockito.any());
    }
}