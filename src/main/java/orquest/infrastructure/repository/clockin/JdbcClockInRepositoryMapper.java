package orquest.infrastructure.repository.clockin;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcClockInRepositoryMapper {

    public ClockIn toClockIn(ResultSet resulSet, int rowNum) throws SQLException {
        return
            new ClockIn(
                resulSet.getLong(1),
                resulSet.getString(2),
                resulSet.getString(3),
                resulSet.getString(4)
            );
    }

    public void add(List<ClockIn> clockIns, List<ClockInRecord> records, List<ClockInAlert> alerts) {
        Map<Long, ClockIn> clockInMapById =
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
    }
}
