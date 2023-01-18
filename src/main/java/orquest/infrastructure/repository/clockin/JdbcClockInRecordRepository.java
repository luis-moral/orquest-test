package orquest.infrastructure.repository.clockin;

import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordRepository;
import reactor.core.publisher.Flux;

public class JdbcClockInRecordRepository implements ClockInRecordRepository {

    @Override
    public Flux<ClockInRecord> forEmployee(String employeeId) {
        throw new UnsupportedOperationException();
    }
}
