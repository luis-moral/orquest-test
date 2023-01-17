package orquest.infrastructure.repository;

import orquest.domain.clockin.ClockInRecord;
import orquest.domain.clockin.ClockInRepository;

import java.util.List;

public class JdbcClockInRepository implements ClockInRepository {
    @Override
    public List<ClockInRecord> getByEmployee(String id) {
        return null;
    }
}
