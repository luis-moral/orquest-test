package orquest.infrastructure.repository.alert;

import orquest.domain.alert.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcAlertRepositoryMapper {

    public Alert toAlert(ResultSet resulSet, int rowNum) throws SQLException {
        return
            new Alert(
                resulSet.getObject(1, UUID.class),
                resulSet.getString(2),
                resulSet.getString(3),
                resulSet.getString(4)
            );
    }
}
