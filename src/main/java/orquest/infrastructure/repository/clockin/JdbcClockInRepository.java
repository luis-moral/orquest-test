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
import orquest.infrastructure.util.sql.SelectBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class JdbcClockInRepository implements ClockInRepository {

    private static SelectBuilder SelectClockIn() {
        return
            new SelectBuilder()
                .field("id", "business_id", "employee_id", "service_id")
                .from("clock_in");
    }

    private static SelectBuilder SelectClockInRecordByClockInId() {
        return
            new SelectBuilder()
                .field("id", "clock_in_id", "date", "type", "action")
                .from("clock_in_record")
                .where("clock_in_id IN(:clock_in_ids)");
    }

    private static SelectBuilder SelectClockInAlertByClockInId() {
        return
            new SelectBuilder()
                .field("id", "clock_in_id", "alert_id")
                .from("clock_in_alert")
                .where("clock_in_id IN(:clock_in_ids)");
    }

    private static final String INSERT_MULTIPLE_CLOCK_IN =
        "INSERT INTO clock_in (business_id, employee_id, service_id) VALUES (:business_id, :employee_id, :service_id)";

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
                    addFilter(SelectClockIn(), filter, parameters).toString(),
                    parameters,
                    mapper::toClockIn
                );

        List<Long> clockInIds = clockIns.stream().map(ClockIn::id).toList();

        return mapper.add(clockIns, records(clockInIds), alerts(clockInIds));
    }

    @Override
    public int create(List<CreateClockIn> clockIns) {
        if (clockIns.isEmpty()) {
            return 0;
        }

        MapSqlParameterSource[] parameters = new MapSqlParameterSource[clockIns.size()];

        IntStream
            .of(clockIns.size())
            .forEach(index -> {
                CreateClockIn createClockIn = clockIns.get(index);

                MapSqlParameterSource parameter = new MapSqlParameterSource("id", UUID.randomUUID().toString());
                parameter.addValue("businessId", createClockIn.businessId());
                parameter.addValue("employeeId", createClockIn.employeeId());
                parameter.addValue("serviceId", createClockIn.serviceId());

                parameters[index] = parameter;
            });

        int[] result = jdbcTemplate.batchUpdate(INSERT_MULTIPLE_CLOCK_IN, parameters);

        return Arrays.stream(result).sum();
    }

    @Override
    public int update(List<UpdateClockIn> clockIns) {
        if (clockIns.isEmpty()) {
            return 0;
        }

        return 0;
    }

    @Override
    public int createAndUpdate(Collection<CreateClockIn> newClockIns, Collection<UpdateClockIn> updatedClockIns) {
        return 0;
    }

    private SelectBuilder addFilter(SelectBuilder builder, ClockInFilter filter, MapSqlParameterSource parameters) {
        if (filter == null) {
            return builder;
        }

        // From-To
        if (filter.from() != null && filter.to() != null) {
            builder.where("date >= :from AND date <= :to");
            parameters.addValue("from", filter.from());
            parameters.addValue("to", filter.to());
        }

        // Business Ids
        if (filter.businessIds().size() == 1) {
            builder.where("business_id = :business_id");
            parameters.addValue("business_id", filter.businessIds().stream().findFirst().get());
        }
        else if (filter.businessIds().size() > 1) {
            builder.where("business_id IN (:business_ids)");
            parameters.addValue("business_ids", filter.businessIds());
        }

        // Employee Ids
        if (filter.employeeIds().size() == 1) {
            builder.where("employee_id = :employee_id");
            parameters.addValue("employee_id", filter.employeeIds().stream().findFirst().get());
        }
        else if (filter.employeeIds().size() > 1) {
            builder.where("employee_id IN (:employee_ids)");
            parameters.addValue("employee_ids", filter.employeeIds());
        }

        return builder;
    }

    private List<ClockInRecord> records(List<Long> clockInIds) {
        return
            jdbcTemplate
                .query(
                    SelectClockInRecordByClockInId().toString(),
                    new MapSqlParameterSource("clock_in_ids", clockInIds),
                    mapper::toClockInRecord
                );
    }

    private List<ClockInAlert> alerts(List<Long> clockInIds) {
        return
            jdbcTemplate
                .query(
                    SelectClockInAlertByClockInId().toString(),
                    new MapSqlParameterSource("clock_in_ids", clockInIds),
                    mapper::toClockInAlert
                );
    }
}
