package orquest.infrastructure.handler.clockin_employee.get;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin_employee.ClockInsByWeek;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;
import orquest.infrastructure.util.validator.RequestParameterValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GetEmployeeClockInHandlerMapperShould {

    private final static UUID ALERT_ONE_ID = UUID.fromString("2baa2295-27ee-4d60-9305-7e2f7e159988");
    private final static UUID ALERT_TWO_ID = UUID.fromString("35ac5f30-f5c4-475c-b7e8-194ae6396c25");

    private final static UUID CLOCK_IN_ONE_ID = UUID.fromString("2d6d2267-9c2c-437b-a763-96e986bd8d84");
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

    private RequestParameterValidator parameterValidator;
    private GetEmployeeClockInHandlerMapper mapper;

    @BeforeEach
    public void setUp() {
        parameterValidator = Mockito.mock(RequestParameterValidator.class);

        mapper = new GetEmployeeClockInHandlerMapper(parameterValidator);
    }

    @Test public void
    map_server_request_to_employee_id() {
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("employeeId1"), "employeeId"))
            .thenReturn("employeeId1");

        Assertions
            .assertThat(mapper.toEmployeeId("employeeId1"))
            .isEqualTo("employeeId1");

        Mockito
            .verify(parameterValidator, Mockito.times(1))
            .mandatoryString(Mockito.any(), Mockito.any());
    }

    @Test public void
    map_clock_ins_by_week_to_get_employee_clock_ins_response() {
        ClockInsByWeek clockInsByWeek = clockInsByWeek();

        GetEmployeeClockInResponse.ClockInWeekResponse expectedFirstWeek =
            new GetEmployeeClockInResponse.ClockInWeekResponse(
                1,
                21600000L,
                List.of(clockInResponse(CLOCK_IN_ONE))
            );

        GetEmployeeClockInResponse.ClockInWeekResponse expectedSecondWeek =
            new GetEmployeeClockInResponse.ClockInWeekResponse(
                3,
                25200000L,
                List.of(clockInResponse(CLOCK_IN_THREE), clockInResponse(CLOCK_IN_FOUR))
            );

        GetEmployeeClockInResponse expected = new GetEmployeeClockInResponse(List.of(expectedFirstWeek, expectedSecondWeek));
        GetEmployeeClockInResponse result = mapper.toGetEmployeeClockInResponse(clockInsByWeek);

        Assertions
            .assertThat(result)
            .isEqualTo(expected);
    }

    private ClockInsByWeek clockInsByWeek() {
        ClockInsByWeek.ClockInWeek firstWeek =
            new ClockInsByWeek.ClockInWeek(
                1,
                CLOCK_IN_ONE.timeWorked(),
                List.of(CLOCK_IN_ONE)
            );

        ClockInsByWeek.ClockInWeek secondWeek =
            new ClockInsByWeek.ClockInWeek(
                3,
                CLOCK_IN_THREE.timeWorked() + CLOCK_IN_FOUR.timeWorked(),
                List.of(CLOCK_IN_THREE, CLOCK_IN_FOUR)
            );

        return new ClockInsByWeek(List.of(firstWeek, secondWeek));
    }

    private GetEmployeeClockInResponse.ClockInResponse clockInResponse(ClockIn clockIn) {
        return
            new GetEmployeeClockInResponse.ClockInResponse(
                clockIn.id(),
                clockIn.businessId(),
                clockIn.employeeId(),
                clockIn.serviceId(),
                clockInRecordResponse(clockIn),
                clockIn.alerts().stream().map(alert -> alert.alertId().toString()).toList()
            );
    }

    private List<GetEmployeeClockInResponse.ClockInRecordResponse> clockInRecordResponse(ClockIn clockIn) {
        return
            clockIn
                .records()
                .stream()
                .map(
                    record ->
                        new GetEmployeeClockInResponse.ClockInRecordResponse(
                            record.date(),
                            record.type().toString(),
                            record.action().toString()
                        )
                )
                .toList();
    }
}