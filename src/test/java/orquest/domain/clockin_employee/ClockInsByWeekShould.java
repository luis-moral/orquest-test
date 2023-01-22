package orquest.domain.clockin_employee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;

import java.util.List;

public class ClockInsByWeekShould {

    @Test public void
    get_business_id() {
        ClockIn clockIn = Mockito.mock(ClockIn.class);
        ClockInsByWeek.ClockInWeek clockInWeek = Mockito.mock(ClockInsByWeek.ClockInWeek.class);
        ClockInsByWeek clockInsByWeek = new ClockInsByWeek(List.of(clockInWeek));

        Mockito
            .when(clockIn.businessId())
            .thenReturn("businessId1");

        Mockito
            .when(clockInWeek.clockIns())
            .thenReturn(List.of(clockIn));

        Assertions
            .assertThat(clockInsByWeek.businessId())
            .isEqualTo("businessId1");
        Assertions
            .assertThat(new ClockInsByWeek(List.of()).businessId())
            .isNull();
    }

    @Test public void
    get_employee_id() {
        ClockIn clockIn = Mockito.mock(ClockIn.class);
        ClockInsByWeek.ClockInWeek clockInWeek = Mockito.mock(ClockInsByWeek.ClockInWeek.class);
        ClockInsByWeek clockInsByWeek = new ClockInsByWeek(List.of(clockInWeek));

        Mockito
            .when(clockIn.employeeId())
            .thenReturn("employeeId1");

        Mockito
            .when(clockInWeek.clockIns())
            .thenReturn(List.of(clockIn));

        Assertions
            .assertThat(clockInsByWeek.employeeId())
            .isEqualTo("employeeId1");
        Assertions
            .assertThat(new ClockInsByWeek(List.of()).employeeId())
            .isNull();
    }
}