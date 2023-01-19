package orquest.infrastructure.repository.clockin;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;

import java.util.Collection;
import java.util.List;

public class JdbcClockInRepository implements ClockInRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClockInRepositoryMapper mapper;

    public JdbcClockInRepository(NamedParameterJdbcTemplate jdbcTemplate, JdbcClockInRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public ClockIn find(String businessId, long id) {
        return null;
    }

    @Override
    public List<ClockIn> find(ClockInFilter filter) {
        return null;
    }

    @Override
    public Long update(List<ClockIn> clockIns) {
        return null;
    }

    @Override
    public Long create(List<CreateClockIn> clockIns) {
        return null;
    }

    @Override
    public Long createAndUpdate(Collection<CreateClockIn> newClockIns, Collection<UpdateClockIn> updatedClockIns) {
        return null;
    }
}
