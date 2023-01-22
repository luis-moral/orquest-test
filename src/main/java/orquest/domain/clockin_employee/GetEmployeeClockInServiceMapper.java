package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;

import java.util.List;
import java.util.Set;

public class GetEmployeeClockInServiceMapper {

    public ClockInFilter toFilter(String employeeId) {
        return new ClockInFilter.Builder().employeeIds(Set.of("employeeId1")).build();
    }

    public ClockInsByWeek toClockInsByWeek(List<ClockIn> clockIns) {
        throw new UnsupportedOperationException();
    }
}
