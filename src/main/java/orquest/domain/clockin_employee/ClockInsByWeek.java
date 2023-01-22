package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockIn;

import java.util.List;

public record ClockInsByWeek(List<ClockInWeek> clockInWeeks) {

    public record ClockInWeek(int weekOfYear, long timeWorked, List<ClockIn> clockIns) {
    }
}
