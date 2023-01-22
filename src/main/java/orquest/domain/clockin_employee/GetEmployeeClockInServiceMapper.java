package orquest.domain.clockin_employee;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.time.TimeRecordGroup;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GetEmployeeClockInServiceMapper {

    public ClockInFilter toFilter(String businessId, String employeeId) {
        return
            new ClockInFilter.Builder()
                .businessIds(Set.of(businessId))
                .employeeIds(Set.of(employeeId))
                .build();
    }

    public ClockInsByWeek toClockInsByWeek(List<ClockIn> clockIns) {
        Map<WeekAndYear, List<ClockIn>> mapByWeek =
            clockIns
                .stream()
                .filter(clockIn -> clockIn.date().isPresent())
                .collect(Collectors.groupingBy(this::weekOfYear));

        return
            new ClockInsByWeek(
                mapByWeek
                    .entrySet()
                    .stream()
                    .map(
                        entry ->
                            new ClockInsByWeek.ClockInWeek(
                                entry.getKey().week(),
                                entry.getKey().year(),
                                timeWorked(entry.getValue()),
                                entry.getValue()
                            )
                    )
                    .toList()
            );
    }

    private WeekAndYear weekOfYear(ClockIn clockIn) {
        LocalDate localDate =
            Instant
                .ofEpochMilli(clockIn.date().get())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();

        return
            new WeekAndYear(
                localDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR),
                localDate.getYear()
            );
    }

    private long timeWorked(List<ClockIn> clockIns) {
        return
            clockIns
                .stream()
                .mapToLong(TimeRecordGroup::timeWorked)
                .sum();
    }

    private record WeekAndYear(int week, int year) {
    }
}
