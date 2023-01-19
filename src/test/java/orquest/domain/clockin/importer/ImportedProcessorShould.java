package orquest.domain.clockin.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.domain.clockin.record.CreateClockInRecord;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ImportedProcessorShould {

    private ImportedProcessorMapper mapper;
    private ImportedProcessor processor;

    @BeforeEach
    public void setUp() {
        mapper = Mockito.mock(ImportedProcessorMapper.class);

        processor = new ImportedProcessor(mapper);
    }

    @Test public void
    map_imported_clock_ins_to_clock_in_filter() {
        ImportedClockIn importedOne = imported("businessId1", 1_500L, "employeeId1", ClockInRecordType.IN, "serviceId1", ClockInRecordAction.WORK);
        ImportedClockIn importedTwo = imported("businessId1", 2_500L, "employeeId1", ClockInRecordType.OUT, "serviceId1", ClockInRecordAction.WORK);
        ImportedClockIn importedThree = imported("businessId1", 10_500L, "employeeId2", ClockInRecordType.IN, "serviceId2", ClockInRecordAction.REST);

        ClockInFilter expected =
            new ClockInFilter.Builder()
                .businessIds(Set.of("businessId1"))
                .from(1_500L)
                .to(10_500L)
                .employeeIds(Set.of("employeeId1", "employeeId2"))
                .build();

        ClockInFilter filter = processor.filter(List.of(importedOne, importedThree, importedTwo));

        Assertions
            .assertThat(filter)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test public void
    group_imported_clock_ins_in_create_clock_ins() {
        ImportedClockIn importedOne = imported("businessId1", 1_500L, "employeeId1", ClockInRecordType.IN, "serviceId1", ClockInRecordAction.WORK);
        ImportedClockIn importedTwo = imported("businessId1", 2_500L, "employeeId1", ClockInRecordType.OUT, "serviceId1", ClockInRecordAction.WORK);
        ImportedClockIn importedThree = imported("businessId1", 10_500L, "employeeId2", ClockInRecordType.IN, "serviceId2", ClockInRecordAction.REST);
        ImportedClockIn importedFour = imported("businessId1", TimeUnit.DAYS.toMillis(2), "employeeId1", ClockInRecordType.OUT, "serviceId2", ClockInRecordAction.REST);
        ImportedClockIn importedFive = imported("businessId2", 2_500L, "employeeId1", ClockInRecordType.IN, "serviceId1", ClockInRecordAction.REST);

        CreateClockIn expectedOne =
            create(
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(
                    createRecord(1_500L, ClockInRecordType.IN, ClockInRecordAction.WORK),
                    createRecord(2_500L, ClockInRecordType.OUT, ClockInRecordAction.WORK)
                ),
                List.of()
            );
        CreateClockIn expectedTwo =
            create(
                "businessId1",
                "employeeId2",
                "serviceId2",
                List.of(createRecord(10_500L, ClockInRecordType.IN, ClockInRecordAction.REST)),
                List.of()
            );
        CreateClockIn expectedThree =
            create(
                "businessId1",
                "employeeId1",
                "serviceId2",
                List.of(createRecord(TimeUnit.DAYS.toMillis(2), ClockInRecordType.OUT, ClockInRecordAction.REST)),
                List.of()
            );
        CreateClockIn expectedFour =
            create(
                "businessId2",
                "employeeId1",
                "serviceId1",
                List.of(createRecord(2_500L, ClockInRecordType.IN, ClockInRecordAction.REST)),
                List.of()
            );

        Mockito
            .when(mapper.toCreateClockIn(Mockito.any()))
            .thenCallRealMethod();
        Mockito
            .when(mapper.toCreateClockInRecord(Mockito.any()))
            .thenCallRealMethod();

        Assertions
            .assertThat(
                processor.group(List.of(importedOne, importedTwo, importedThree, importedFour, importedFive))
            )
            .containsExactlyInAnyOrder(
                expectedOne,
                expectedTwo,
                expectedThree,
                expectedFour
            );

        Mockito
            .verify(mapper, Mockito.times(4))
            .toCreateClockIn(Mockito.any());
        Mockito
            .verify(mapper, Mockito.times(5))
            .toCreateClockInRecord(Mockito.any());
    }

    @Test public void
    merge_imported_clock_ins_to_check_alerts_and_persist_the_data() {
        CreateClockIn createClockInOne =
            create(
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(
                    createRecord(1_500L, ClockInRecordType.IN, ClockInRecordAction.WORK),
                    createRecord(2_500L, ClockInRecordType.OUT, ClockInRecordAction.WORK)
                ),
                List.of()
            );
        CreateClockIn createClockInTwo =
            create(
                "businessId1",
                "employeeId2",
                "serviceId1",
                List.of(createRecord(10_500L, ClockInRecordType.IN, ClockInRecordAction.REST)),
                List.of()
            );
        CreateClockIn createClockInThree =
            create(
                "businessId1",
                "employeeId1",
                "serviceId2",
                List.of(createRecord(TimeUnit.DAYS.toMillis(2), ClockInRecordType.OUT, ClockInRecordAction.REST)),
                List.of()
            );
        CreateClockIn createClockInFour =
            create(
                "businessId2",
                "employeeId1",
                "serviceId1",
                List.of(createRecord(2_500L, ClockInRecordType.IN, ClockInRecordAction.REST)),
                List.of()
            );

        ClockIn clockInOne =
            clockIn(
                1L,
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(clockInRecord(1L, 1L, 50_000L, ClockInRecordType.IN, ClockInRecordAction.REST)),
                List.of()
            );
        ClockIn clockInTwo =
            clockIn(
                2L,
                "businessId1",
                "employeeId1",
                "serviceId2",
                List
                    .of(
                        clockInRecord(2L, 2L, 50_000L, ClockInRecordType.IN, ClockInRecordAction.REST),
                        clockInRecord(3L, 2L, 60_000L, ClockInRecordType.OUT, ClockInRecordAction.REST)
                    ),
                List.of()
            );
        ClockIn clockInThree =
            clockIn(
                3L,
                "businessId1",
                "employeeId1",
                "serviceId2",
                List
                    .of(
                        clockInRecord(4L, 3L, 5_000_000L, ClockInRecordType.IN, ClockInRecordAction.REST),
                        clockInRecord(5L, 3L, 6_000_000L, ClockInRecordType.OUT, ClockInRecordAction.REST)
                    ),
                List.of()
            );

        Assertions
            .assertThat(
                processor
                    .merge(
                        List.of(createClockInOne, createClockInTwo, createClockInThree, createClockInFour),
                        List.of(clockInOne, clockInTwo, clockInThree)
                    )
            )
            .isEqualTo(
                Tuples.of(
                    List.of(createClockInThree, createClockInFour),
                    List.of(toUpdate(clockInOne), toUpdate(clockInTwo))
                )
            );
    }

    @Test public void
    check_for_alerts() {
        /*CreateClockIn createClockInOne = create("businessId1", "employeeId1", "serviceId1", List.of(), List.of());
        CreateClockIn createClockInTwo = create("businessId1", "employeeId2", "serviceId2", List.of(), List.of());

        ClockIn clockInOne = clockIn(1L, "businessId1", "employeeId1", "serviceId1", List.of(), List.of());
        ClockIn clockInTwo = clockIn(2L, "businessId1", "employeeId1", "serviceId2", List.of(), List.of());

        Alert alertOne = Mockito.mock(Alert.class);
        Alert alertTwo = Mockito.mock(Alert.class);

        Tuple2<List<CreateClockIn>, List<UpdateClockIn>> expected =
            Tuples.of(
                List.of(copyReplacingAlerts(createClockInOne, List.of(1L)), copyReplacingAlerts(createClockInTwo, List.of(2L))),
                List.of(toUpdateReplacingAlerts(clockInOne, List.of()), toUpdateReplacingAlerts(clockInTwo, List.of(2L)))
            );

        Mockito
            .verify(alertOne, Mockito.times(2))
            .checkFor(Mockito.any(ClockIn.class));
        Mockito
            .verify(alertTwo, Mockito.times(2))
            .checkFor(Mockito.any(CreateClockIn.class));
        Mockito
            .verify(alertTwo, Mockito.times(2))
            .checkFor(Mockito.any(ClockIn.class));
        Mockito
            .verify(alertTwo, Mockito.times(2))
            .checkFor(Mockito.any(CreateClockIn.class));*/
    }

    private ImportedClockIn imported(
        String businessId,
        long date,
        String employeeId,
        ClockInRecordType type,
        String serviceId,
        ClockInRecordAction action
    ) {
        return
            new ImportedClockIn(
                businessId,
                Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC),
                employeeId,
                type,
                serviceId,
                action
            );
    }

    private CreateClockIn create(
        String businessId,
        String employeeId,
        String serviceId,
        List<CreateClockInRecord> records,
        List<CreateClockInAlert> alerts
    ) {
        return
            new CreateClockIn(
                businessId,
                employeeId,
                serviceId,
                records,
                alerts
            );
    }

    private CreateClockInRecord createRecord(
        long date,
        ClockInRecordType type,
        ClockInRecordAction action
    ) {
        return
            new CreateClockInRecord(
                date,
                type,
                action
            );
    }

    private ClockIn clockIn(
        long id,
        String businessId,
        String employeeId,
        String serviceId,
        List<ClockInRecord> records,
        List<ClockInAlert> alerts
    ) {
        return
            new ClockIn(
                id,
                businessId,
                employeeId,
                serviceId,
                records,
                alerts
            );
    }

    private ClockInRecord clockInRecord(
        long id,
        long clockInId,
        long date,
        ClockInRecordType type,
        ClockInRecordAction action
    ) {
        return
            new ClockInRecord(
                id,
                clockInId,
                date,
                type,
                action
            );
    }

    private CreateClockIn copyReplacingAlerts(CreateClockIn createClockIn, List<Long> alerts) {
        return
            create(
                createClockIn.businessId(),
                createClockIn.employeeId(),
                createClockIn.serviceId(),
                createClockIn.records(),
                alerts.stream().map(CreateClockInAlert::new).collect(Collectors.toList())
            );
    }

    private UpdateClockIn copyReplacingAlerts(UpdateClockIn clockIn, List<Long> alerts) {
        return
            new UpdateClockIn(
                clockIn.id(),
                clockIn.records(),
                alerts.stream().map(CreateClockInAlert::new).collect(Collectors.toList())
            );
    }

    private UpdateClockIn toUpdate(ClockIn clockIn) {
        return
            new UpdateClockIn(
                clockIn.id(),
                clockIn
                    .records()
                    .stream()
                    .map(record -> createRecord(record.date(), record.type(), record.action()))
                    .collect(Collectors.toList()),
                clockIn.alerts().stream().map(alert -> new CreateClockInAlert(alert.alertId())).collect(Collectors.toList())
            );
    }
}