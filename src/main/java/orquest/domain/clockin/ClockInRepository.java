package orquest.domain.clockin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClockInRepository {

    Flux<ClockIn> find(ClockInFilter filter);

    Flux<ClockIn> forEmployee(String businessId, String employeeId);

    Mono<Long> create(List<CreateClockIn> clockIns);
}
