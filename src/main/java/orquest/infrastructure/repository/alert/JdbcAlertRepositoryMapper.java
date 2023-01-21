package orquest.infrastructure.repository.alert;

import orquest.domain.alert.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcAlertRepositoryMapper {

    public Alert toAlert(ResultSet resultSet, int rowNum) throws SQLException {
        return
            new Alert(
                resultSet.getObject(1, UUID.class),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4)
            );
    }
}
