package orquest.domain.clockin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ClockInRepository {

    Flux<ClockInRecord> getByEmployee(String id);

    Mono<Long> create(List<ImportedClockIn> clockIns);
}
