package orquest.infrastructure.repository.clockin;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;

import java.util.Collection;
import java.util.List;

public class JdbcClockInRepository implements ClockInRepository {

    private final static String SELECT_CLOCK_IN =
        "SELECT id, business_id, employee_id, service_id FROM clock_in";

    private final static String SELECT_CLOCK_IN_RECORD =
        "SELECT id, clock_in_id, date, type, action FROM clock_in_record";
    private final static String SELECT_CLOCK_IN_RECORD_BY_CLOCK_IN_ID =
        SELECT_CLOCK_IN_RECORD + " WHERE clock_in_id IN(:clock_in_ids)";

    private final static String SELECT_CLOCK_IN_ALERT =
        "SELECT id, clock_in_id, alert_id FROM clock_in_alert";
    private final static String SELECT_CLOCK_IN_ALERT_BY_CLOCK_IN_ID =
        SELECT_CLOCK_IN_ALERT + " WHERE clock_in_id IN(:clock_in_ids)";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClockInRepositoryMapper mapper;

    public JdbcClockInRepository(NamedParameterJdbcTemplate jdbcTemplate, JdbcClockInRepositoryMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public List<ClockIn> find(ClockInFilter filter) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        List<ClockIn> clockIns =
            jdbcTemplate
                .query(
                    addFilter(SELECT_CLOCK_IN, filter, parameters),
                    parameters,
                    mapper::toClockIn
                );

        List<Long> clockInIds = clockIns.stream().map(ClockIn::id).toList();

        return mapper.add(clockIns, records(clockInIds), alerts(clockInIds));
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

    private List<ClockInRecord> records(List<Long> clockInIds) {
        return
            jdbcTemplate
                .query(
                    SELECT_CLOCK_IN_RECORD_BY_CLOCK_IN_ID,
                    new MapSqlParameterSource("clock_in_ids", clockInIds),
                    mapper::toClockInRecord
                );
    }

    private List<ClockInAlert> alerts(List<Long> clockInIds) {
        return
            jdbcTemplate
                .query(
                    SELECT_CLOCK_IN_ALERT_BY_CLOCK_IN_ID,
                    new MapSqlParameterSource("clock_in_ids", clockInIds),
                    mapper::toClockInAlert
                );
    }
}
