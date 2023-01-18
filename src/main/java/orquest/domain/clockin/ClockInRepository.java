package orquest.domain.clockin;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.List;

public interface ClockInRepository {

    Flux<ClockIn> find(String businessId, long id);

    default Flux<ClockIn> find() {
        return find(null);
    }

    Flux<ClockIn> find(@Nullable ClockInFilter filter);

    Mono<Long> create(List<CreateClockIn> clockIns);
}
