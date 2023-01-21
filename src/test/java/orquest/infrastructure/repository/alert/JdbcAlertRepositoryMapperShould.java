package orquest.infrastructure.repository.alert;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.alert.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcAlertRepositoryMapperShould {

    private ResultSet resultSet;
    private JdbcAlertRepositoryMapper mapper;

    @BeforeEach
    public void setUp() {
        resultSet = Mockito.mock(ResultSet.class);

        mapper = new JdbcAlertRepositoryMapper();
    }

    @Test public void
    map_result_set_to_alert() throws SQLException {
        UUID alertId = UUID.randomUUID();
        Alert expected = new Alert(alertId, "A", "B", "C");

        Mockito.when(resultSet.getObject(1, UUID.class)).thenReturn(alertId);
        Mockito.when(resultSet.getString(2)).thenReturn("A");
        Mockito.when(resultSet.getString(3)).thenReturn("B");
        Mockito.when(resultSet.getString(4)).thenReturn("C");

        Assertions
            .assertThat(mapper.toAlert(resultSet, 1))
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
}