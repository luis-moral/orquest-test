package orquest.infrastructure.repository.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcClockInRepositoryMapperShould {

    private ResultSet resultSet;
    private JdbcClockInRepositoryMapper mapper;
    
    @BeforeEach
    public void setUp() {
        resultSet = Mockito.mock(ResultSet.class);

        mapper = new JdbcClockInRepositoryMapper();
    }

    @Test public void
    map_result_set_to_clock_in() throws SQLException {
        ClockIn expected =
            new ClockIn(
                5L,
                "A",
                "B",
                "C",
                List.of(),
                List.of()
            );

        Mockito.when(resultSet.getLong(1)).thenReturn(5L);
        Mockito.when(resultSet.getString(2)).thenReturn("A");
        Mockito.when(resultSet.getString(3)).thenReturn("B");
        Mockito.when(resultSet.getString(4)).thenReturn("C");

        Assertions
            .assertThat(mapper.toClockIn(resultSet, 1))
            .isEqualTo(expected);
    }
}