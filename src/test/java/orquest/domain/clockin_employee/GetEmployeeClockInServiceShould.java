package orquest.domain.clockin_employee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GetEmployeeClockInServiceShould {

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
                    TimeUnit.DAYS.toMillis(20L) + TimeUnit.HOURS.toMillis(5L),
                    TimeRecordType.IN,
                    TimeRecordAction.REST
                ),
                new ClockInRecord(
                    CLOCK_IN_THREE_ID,
                    TimeUnit.DAYS.toMillis(20L) + TimeUnit.HOURS.toMillis(5L) + TimeUnit.HOURS.toMillis(3L),
                    TimeRecordType.OUT,
                    TimeRecordAction.REST
                )
            ),
            List.of()
        );

    private ClockInRepository clockInRepository;
    private GetEmployeeClockInServiceMapper mapper;
    private GetEmployeeClockInService service;

    @BeforeEach
    public void setUp() {
        clockInRepository = Mockito.mock(ClockInRepository.class);
        mapper = Mockito.mock(GetEmployeeClockInServiceMapper.class);

        service = new GetEmployeeClockInService(clockInRepository, mapper);
    }

    @Test public void
    return_employee_time_records_grouped_by_week() {
        ClockInFilter filter = new ClockInFilter.Builder().employeeIds(Set.of("employeeId1")).build();
        List<ClockIn> clockIns = List.of(CLOCK_IN_ONE, CLOCK_IN_TWO, CLOCK_IN_THREE);

        ClockInsByWeek expected = Mockito.mock(ClockInsByWeek.class);

        Mockito
            .when(mapper.toFilter("employeeId1"))
            .thenReturn(filter);

        Mockito
            .when(clockInRepository.find(filter))
            .thenReturn(clockIns);

        Mockito
            .when(mapper.toClockInsByWeek(clockIns))
            .thenReturn(expected);

        StepVerifier
            .create(service.getByWeek("employeeId1"))
            .assertNext(
                clockInsByWeek ->
                    Assertions
                        .assertThat(clockInsByWeek)
                        .isEqualTo(expected)
            )
            .verifyComplete();

        Mockito
            .verify(mapper, Mockito.times(1))
            .toFilter(Mockito.any());

        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .find(Mockito.any());

        Mockito
            .verify(mapper, Mockito.times(1))
            .toClockInsByWeek(Mockito.any());
    }
}