package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockInRepository;
import reactor.core.publisher.Mono;

public class GetEmployeeClockInService {

    private final ClockInRepository clockInRepository;
    private final GetEmployeeClockInServiceMapper mapper;

    public GetEmployeeClockInService(
        ClockInRepository clockInRepository,
        GetEmployeeClockInServiceMapper mapper
    ) {
        this.clockInRepository = clockInRepository;
        this.mapper = mapper;
    }

    public Mono<ClockInsByWeek> getByWeek(String businessId, String employeeId) {
        return
            Mono
                .fromCallable(() -> clockInRepository.find(mapper.toFilter(businessId, employeeId)))
                .map(mapper::toClockInsByWeek);
    }
}
