package orquest.infrastructure.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.ImportedClockIn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class JdbcClockInRepository implements ClockInRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClockInRepositoryMapper mapper;

    public JdbcClockInRepository(NamedParameterJdbcTemplate jdbcTemplate, JdbcClockInRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Flux<ClockIn> forEmployee(String employeeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Long> create(List<ImportedClockIn> clockIns) {
        throw new UnsupportedOperationException();
    }
}
