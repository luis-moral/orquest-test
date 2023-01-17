package orquest.infrastructure.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockInRecord;
import orquest.domain.clockin.ClockInRepository;

import java.util.List;

public class JdbcClockInRepository implements ClockInRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClockInRepositoryMapper mapper;

    public JdbcClockInRepository(NamedParameterJdbcTemplate jdbcTemplate, JdbcClockInRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<ClockInRecord> getByEmployee(String id) {
        throw new UnsupportedOperationException();
    }
}
