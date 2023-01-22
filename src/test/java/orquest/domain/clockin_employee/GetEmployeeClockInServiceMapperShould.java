package orquest.domain.clockin_employee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GetEmployeeClockInServiceMapperShould {

    private final static UUID ALERT_ONE_ID = UUID.fromString("2baa2295-27ee-4d60-9305-7e2f7e159988");
    private final static UUID ALERT_TWO_ID = UUID.fromString("35ac5f30-f5c4-475c-b7e8-194ae6396c25");

    private final static UUID CLOCK_IN_ONE_ID = UUID.fromString("2d6d2267-9c2c-437b-a763-96e986bd8d84");
    private final static UUID CLOCK_IN_TWO_ID = UUID.fromString("af0a0ef1-9974-4c64-968f-347a5568d5ca");
    private final static UUID CLOCK_IN_THREE_ID = UUID.fromString("52cd4d03-2875-4530-96bf-1c5620fdeed2");

    private final static ClockIn CLOCK_IN_ONE =
        new ClockIn(
            CLOCK_IN_ONE_ID,
            "businessId1",
            "employeeId1",
            "serviceId1",
            List.of(
                new ClockInRecord(
                    CLOCK_IN_ONE_ID,
                    TimeUnit.DAYS.toMillis(3L) + TimeUnit.HOURS.toMillis(5L),
                    TimeRecordType.IN,
                    TimeRecordAction.WORK
                ),
                new ClockInRecord(
                    CLOCK_IN_ONE_ID,
                    TimeUnit.DAYS.toMillis(3L) + TimeUnit.HOURS.toMillis(5L) + TimeUnit.HOURS.toMillis(6L),
                    TimeRecordType.OUT,
                    TimeRecordAction.WORK
                )
            ),
            List.of(
                new ClockInAlert(
                    CLOCK_IN_ONE_ID,
                    ALERT_ONE_ID
                ),
                new ClockInAlert(
                    CLOCK_IN_ONE_ID,
                    ALERT_TWO_ID
                )
            )
        );
    private final static ClockIn CLOCK_IN_TWO =
        new ClockIn(
            CLOCK_IN_TWO_ID,
            "businessId1",
            "employeeId1",
            "serviceId1",
            List.of(),
            List.of()
        );
    private final static ClockIn CLOCK_IN_THREE =
        new ClockIn(
            CLOCK_IN_THREE_ID,
            "businessId1",
            "employeeId1",
            "serviceId2",
            List.of(
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    TimeUnit.DAYS.toMillis(18L) + TimeUnit.HOURS.toMillis(5L),
                    TimeRecordType.IN,
                    TimeRecordAction.WORK
                ),
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    TimeUnit.DAYS.toMillis(18L) + TimeUnit.HOURS.toMillis(5L) + TimeUnit.HOURS.toMillis(3L),
                    TimeRecordType.OUT,
                    TimeRecordAction.WORK
                )
            ),
            List.of()
        );
    private final static ClockIn CLOCK_IN_FOUR =
        new ClockIn(
            CLOCK_IN_THREE_ID,
            "businessId1",
            "employeeId1",
            "serviceId2",
            List.of(
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    TimeUnit.DAYS.toMillis(20L) + TimeUnit.HOURS.toMillis(5L),
                    TimeRecordType.IN,
                    TimeRecordAction.WORK
                ),
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    TimeUnit.DAYS.toMillis(20L) + TimeUnit.HOURS.toMillis(5L) + TimeUnit.HOURS.toMillis(4L),
                    TimeRecordType.OUT,
                    TimeRecordAction.WORK
                )
            ),
            List.of()
        );
    
    private GetEmployeeClockInServiceMapper mapper;
    
    @BeforeEach
    public void setUp() {
        mapper = new GetEmployeeClockInServiceMapper();
    }
    
    @Test public void 
    map_employee_id_to_clock_in_filter() {
        ClockInFilter expected = new ClockInFilter.Builder().employeeIds(Set.of("employeeId1")).build();
        
        Assertions
            .assertThat(mapper.toFilter("employeeId1"))
            .isEqualTo(expected);
    }

    @Test public void
    map_clock_ins_to_clock_ins_by_week() {
        ClockInsByWeek.ClockInWeek firstWeek =
            new ClockInsByWeek.ClockInWeek(
                1,
                1970,
                CLOCK_IN_ONE.timeWorked(),
                List.of(CLOCK_IN_ONE)
            );

        ClockInsByWeek.ClockInWeek secondWeek =
            new ClockInsByWeek.ClockInWeek(
                3,
                1970,
                CLOCK_IN_THREE.timeWorked() + CLOCK_IN_FOUR.timeWorked(),
                List.of(CLOCK_IN_THREE, CLOCK_IN_FOUR)
            );

        ClockInsByWeek expected = new ClockInsByWeek(List.of(firstWeek, secondWeek));
        ClockInsByWeek result = mapper.toClockInsByWeek(List.of(CLOCK_IN_ONE, CLOCK_IN_TWO, CLOCK_IN_THREE, CLOCK_IN_FOUR));

        Assertions
            .assertThat(expected)
            .isEqualTo(result);
    }
}