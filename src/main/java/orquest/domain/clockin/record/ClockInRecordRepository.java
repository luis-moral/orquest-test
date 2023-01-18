package orquest.domain.clockin.record;

import reactor.core.publisher.Flux;

public interface ClockInRecordRepository {

    Flux<ClockInRecord> forEmployee(String employeeId);
}
