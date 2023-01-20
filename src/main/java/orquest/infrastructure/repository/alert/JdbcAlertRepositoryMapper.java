package orquest.infrastructure.repository.alert;

import orquest.domain.alert.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcAlertRepositoryMapper {

    public Alert toAlert(ResultSet resulSet, int rowNum) throws SQLException {
        return
            new Alert(
                resulSet.getLong(1),
                resulSet.getString(2),
                resulSet.getString(3),
                resulSet.getString(4)
            );
    }
}
