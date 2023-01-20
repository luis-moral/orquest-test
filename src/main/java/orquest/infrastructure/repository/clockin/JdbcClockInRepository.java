package orquest.infrastructure.repository.clockin;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;

import java.util.Collection;
import java.util.List;

public class JdbcClockInRepository implements ClockInRepository {

    private final static String SELECT_CLOCK_IN =
        "SELECT id, business_id, employee_id, service_id FROM clock_in";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClockInRepositoryMapper mapper;

    public JdbcClockInRepository(NamedParameterJdbcTemplate jdbcTemplate, JdbcClockInRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<ClockIn> find(ClockInFilter filter) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        return
            jdbcTemplate
                .query(
                    addFilter(SELECT_CLOCK_IN, filter, parameters),
                    parameters,
                    (resulSet, rowNum) -> mapper.toClockIn(resulSet, rowNum)
                );
    }

    @Override
    public Long create(List<CreateClockIn> clockIns) {
        return null;
    }

    @Override
    public Long createAndUpdate(Collection<CreateClockIn> newClockIns, Collection<UpdateClockIn> updatedClockIns) {
        return null;
    }

    private String addFilter(String query, ClockInFilter filter, MapSqlParameterSource parameters) {
        return query;
    }
}
