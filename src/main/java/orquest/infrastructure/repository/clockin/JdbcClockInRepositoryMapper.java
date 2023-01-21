package orquest.infrastructure.repository.clockin;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JdbcClockInRepositoryMapper {

    public ClockIn toClockIn(ResultSet resultSet, int rowNum) throws SQLException {
        return
            new ClockIn(
                resultSet.getObject(1, UUID.class),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4)
            );
    }

    public ClockInRecord toClockInRecord(ResultSet resultSet, int rowNum) throws SQLException {
        return
            new ClockInRecord(
                resultSet.getObject(1, UUID.class),
                resultSet.getLong(2),
                TimeRecordType.valueOf(resultSet.getString(3)),
                TimeRecordAction.valueOf(resultSet.getString(4))
            );
    }

    public ClockInAlert toClockInAlert(ResultSet resultSet, int rowNum) throws SQLException {
        return
            new ClockInAlert(
                    resultSet.getObject(1, UUID.class),
                    resultSet.getObject(2, UUID.class)
                );
    }

    public List<ClockIn> add(List<ClockIn> clockIns, List<ClockInRecord> records, List<ClockInAlert> alerts) {
        Map<UUID, ClockIn> clockInMapById =
            clockIns
                .stream()
                .collect(Collectors.toMap(ClockIn::id, clockIn -> clockIn));

        records
            .forEach(
                record -> clockInMapById.get(record.clockInId()).records().add(record)
            );

        alerts
            .forEach(
                alert -> clockInMapById.get(alert.clockInId()).alerts().add(alert)
            );

        return clockIns;
    }
}
