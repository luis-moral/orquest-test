package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockInRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public class GetEmployeeClockInService {

    private final ClockInRepository clockInRepository;

    public GetEmployeeClockInService(ClockInRepository clockInRepository) {
        this.clockInRepository = clockInRepository;
    }

    public Flux<List<ClockInsByWeek>> getByWeek(long toEmployeeId) {
        throw new UnsupportedOperationException();
    }
}
