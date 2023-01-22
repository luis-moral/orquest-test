package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.time.TimeRecordGroup;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GetEmployeeClockInServiceMapper {

    public ClockInFilter toFilter(String employeeId) {
        return new ClockInFilter.Builder().employeeIds(Set.of(employeeId)).build();
    }

    public ClockInsByWeek toClockInsByWeek(List<ClockIn> clockIns) {
        Map<Integer, List<ClockIn>> clockInsByWeek =
            clockIns
                .stream()
                .filter(clockIn -> clockIn.date().isPresent())
                .collect(Collectors.groupingBy(this::weekOfYear));

        return
            new ClockInsByWeek(
                clockInsByWeek
                    .entrySet()
                    .stream()
                    .map(
                        entry ->
                            new ClockInsByWeek.ClockInWeek(
                                entry.getKey(),
                                timeWorked(entry.getValue()),
                                entry.getValue()
                            )
                    )
                    .toList()
            );
    }

    private int weekOfYear(ClockIn clockIn) {
        return
            Instant
                .ofEpochMilli(clockIn.date().get())
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
                .get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    private long timeWorked(List<ClockIn> clockIns) {
        return
            clockIns
                .stream()
                .mapToLong(TimeRecordGroup::timeWorked)
                .sum();
    }
}
