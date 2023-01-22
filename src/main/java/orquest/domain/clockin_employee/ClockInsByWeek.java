package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockIn;

import java.util.List;

public record ClockInsByWeek(List<ClockInWeek> clockInWeeks) {

    public String businessId() {
        if (clockInWeeks.isEmpty()) {
            return null;
        }

        return clockInWeeks.get(0).clockIns().get(0).businessId();
    }

    public String employeeId() {
        if (clockInWeeks.isEmpty()) {
            return null;
        }

        return clockInWeeks.get(0).clockIns().get(0).employeeId();
    }

    public record ClockInWeek(int weekOfYear, int year, long timeWorked, List<ClockIn> clockIns) {
    }
}
