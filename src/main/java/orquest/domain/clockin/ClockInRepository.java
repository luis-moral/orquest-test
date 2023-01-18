package orquest.domain.clockin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClockInRepository {

    Flux<ClockIn> forEmployee(String employeeId);

    Mono<Long> create(List<ImportedClockIn> clockIns);
}
