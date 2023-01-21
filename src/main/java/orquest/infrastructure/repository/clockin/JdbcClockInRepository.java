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
import orquest.infrastructure.util.generator.IdGenerator;
import orquest.infrastructure.util.sql.SelectBuilder;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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
                .field("clock_in_id", "date", "type", "action")
                .from("clock_in_record")
                .where("clock_in_id IN(:clock_in_ids)");
    }

    private static SelectBuilder SelectClockInAlertByClockInId() {
        return
            new SelectBuilder()
                .field("clock_in_id", "alert_id")
                .from("clock_in_alert")
                .where("clock_in_id IN(:clock_in_ids)");
    }

    private static final String INSERT_CLOCK_IN =
        "INSERT INTO clock_in (id, business_id, employee_id, service_id) VALUES (:id, :business_id, :employee_id, :service_id)";

    private static final String INSERT_CLOCK_IN_RECORD =
        "INSERT INTO clock_in_record (clock_in_id, date, type, action) VALUES (:clock_in_id, :date, :type, :action)";

    private static final String INSERT_CLOCK_IN_ALERT =
        "INSERT INTO clock_in_alert (clock_in_id, alert_id) VALUES (:clock_in_id, :alert_id)";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClockInRepositoryMapper mapper;
    private final IdGenerator idGenerator;

    public JdbcClockInRepository(
        NamedParameterJdbcTemplate jdbcTemplate,
        JdbcClockInRepositoryMapper mapper,
        IdGenerator idGenerator
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
        this.idGenerator = idGenerator;
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

        List<UUID> clockInIds = clockIns.stream().map(ClockIn::id).toList();

        return mapper.add(clockIns, records(clockInIds), alerts(clockInIds));
    }

    @Override
    public int create(List<CreateClockIn> clockIns) {
        if (clockIns.isEmpty()) {
            return 0;
        }

        MapSqlParameterSource[] parameters = new MapSqlParameterSource[clockIns.size()];
        List<Tuple2<CreateClockIn, String>> created = new ArrayList<>(clockIns.size());

        IntStream
            .range(0, clockIns.size())
            .forEach(index -> {
                CreateClockIn createClockIn = clockIns.get(index);
                String id = idGenerator.generateId().toString();

                MapSqlParameterSource parameter = new MapSqlParameterSource("id", id);
                parameter.addValue("business_id", createClockIn.businessId());
                parameter.addValue("employee_id", createClockIn.employeeId());
                parameter.addValue("service_id", createClockIn.serviceId());

                parameters[index] = parameter;
                created.add(Tuples.of(createClockIn, id));
            });

        int[] result = jdbcTemplate.batchUpdate(INSERT_CLOCK_IN, parameters);

        createRecordsAndAlerts(created);

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

    private List<ClockInRecord> records(List<UUID> clockInIds) {
        return
            jdbcTemplate
                .query(
                    SelectClockInRecordByClockInId().toString(),
                    new MapSqlParameterSource("clock_in_ids", clockInIds),
                    mapper::toClockInRecord
                );
    }

    private List<ClockInAlert> alerts(List<UUID> clockInIds) {
        return
            jdbcTemplate
                .query(
                    SelectClockInAlertByClockInId().toString(),
                    new MapSqlParameterSource("clock_in_ids", clockInIds),
                    mapper::toClockInAlert
                );
    }

    private void createRecordsAndAlerts(List<Tuple2<CreateClockIn, String>> clockInsCreated) {
        List<MapSqlParameterSource> recordParameters = new LinkedList<>();
        List<MapSqlParameterSource> alertParameters = new LinkedList<>();

        clockInsCreated
            .forEach(clockInCreated -> {
                String clockInId = clockInCreated.getT2();

                clockInCreated
                    .getT1()
                    .records()
                    .forEach(
                        record -> {
                            MapSqlParameterSource parameter = new MapSqlParameterSource("clock_in_id", clockInId);
                            parameter.addValue("date", record.date());
                            parameter.addValue("type", record.type().toString());
                            parameter.addValue("action", record.action().toString());

                            recordParameters.add(parameter);
                        }
                    );

                clockInCreated
                    .getT1()
                    .alerts()
                    .forEach(
                        alert -> {
                            MapSqlParameterSource parameter = new MapSqlParameterSource("clock_in_id", clockInId);
                            parameter.addValue("alert_id", alert.alertId());

                            alertParameters.add(parameter);
                        }
                    );
            });


        jdbcTemplate.batchUpdate(INSERT_CLOCK_IN_RECORD, recordParameters.toArray(MapSqlParameterSource[]::new));
        jdbcTemplate.batchUpdate(INSERT_CLOCK_IN_ALERT, alertParameters.toArray(MapSqlParameterSource[]::new));
    }
}
