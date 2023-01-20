package orquest.infrastructure.repository.clockin;

import orquest.domain.clockin.ClockIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcClockInRepositoryMapper {

    public ClockIn toClockIn(ResultSet resulSet, int rowNum) throws SQLException {
        return
            new ClockIn(
                resulSet.getLong(1),
                resulSet.getString(2),
                resulSet.getString(3),
                resulSet.getString(4),
                List.of(),
                List.of()
            );
    }
}
