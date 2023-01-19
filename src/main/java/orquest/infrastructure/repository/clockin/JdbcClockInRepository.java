package orquest.infrastructure.repository.clockin;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.CreateClockIn;
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
    public Flux<ClockIn> find(String businessId, long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flux<ClockIn> find(ClockInFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Long> update(List<ClockIn> clockIns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Long> create(List<CreateClockIn> clockIns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<Long> createAndUpdate(List<CreateClockIn> newClockIns, List<ClockIn> updatedClockIns) {
        throw new UnsupportedOperationException();
    }
}
