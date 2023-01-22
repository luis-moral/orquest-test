package orquest.domain.clockin.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.alert.Alert;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.CreateClockInRecord;
import orquest.domain.time.TimeRecordAction;
import orquest.domain.time.TimeRecordType;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        ImportedClockIn importedOne = imported("businessId1", 1_500L, "employeeId1", TimeRecordType.IN, "serviceId1", TimeRecordAction.WORK);
        ImportedClockIn importedTwo = imported("businessId1", 2_500L, "employeeId1", TimeRecordType.OUT, "serviceId1", TimeRecordAction.WORK);
        ImportedClockIn importedThree = imported("businessId1", 10_500L, "employeeId2", TimeRecordType.IN, "serviceId2", TimeRecordAction.REST);

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
        ImportedClockIn importedOne = imported("businessId1", 1_500L, "employeeId1", TimeRecordType.IN, "serviceId1", TimeRecordAction.WORK);
        ImportedClockIn importedTwo = imported("businessId1", 2_500L, "employeeId1", TimeRecordType.OUT, "serviceId1", TimeRecordAction.WORK);
        ImportedClockIn importedThree = imported("businessId1", 10_500L, "employeeId2", TimeRecordType.IN, "serviceId2", TimeRecordAction.REST);
        ImportedClockIn importedFour = imported("businessId1", TimeUnit.DAYS.toMillis(2), "employeeId1", TimeRecordType.OUT, "serviceId2", TimeRecordAction.REST);
        ImportedClockIn importedFive = imported("businessId2", 2_500L, "employeeId1", TimeRecordType.IN, "serviceId1", TimeRecordAction.REST);

        CreateClockIn expectedOne =
            create(
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(
                    createRecord(1_500L, TimeRecordType.IN, TimeRecordAction.WORK),
                    createRecord(2_500L, TimeRecordType.OUT, TimeRecordAction.WORK)
                ),
                List.of()
            );
        CreateClockIn expectedTwo =
            create(
                "businessId1",
                "employeeId2",
                "serviceId2",
                List.of(createRecord(10_500L, TimeRecordType.IN, TimeRecordAction.REST)),
                List.of()
            );
        CreateClockIn expectedThree =
            create(
                "businessId1",
                "employeeId1",
                "serviceId2",
                List.of(createRecord(TimeUnit.DAYS.toMillis(2), TimeRecordType.OUT, TimeRecordAction.REST)),
                List.of()
            );
        CreateClockIn expectedFour =
            create(
                "businessId2",
                "employeeId1",
                "serviceId1",
                List.of(createRecord(2_500L, TimeRecordType.IN, TimeRecordAction.REST)),
                List.of()
            );

        Mockito
            .when(mapper.toCreateClockIn(Mockito.any()))
            .thenCallRealMethod();
        Mockito
            .when(mapper.toCreateClockInRecord(Mockito.any(ImportedClockIn.class)))
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
            .toCreateClockInRecord(Mockito.any(ImportedClockIn.class));
    }

    @Test public void
    merge_imported_clock_ins_with_current_clock_ins() {
        UUID clockInOneId = UUID.randomUUID();
        UUID clockInTwoId = UUID.randomUUID();
        UUID clockInThreeId = UUID.randomUUID();

        CreateClockIn createClockInOne =
            create(
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(
                    createRecord(1_500L, TimeRecordType.IN, TimeRecordAction.WORK),
                    createRecord(2_500L, TimeRecordType.OUT, TimeRecordAction.WORK)
                ),
                List.of()
            );
        CreateClockIn createClockInTwo =
            create(
                "businessId1",
                "employeeId2",
                "serviceId1",
                List.of(createRecord(10_500L, TimeRecordType.IN, TimeRecordAction.REST)),
                List.of()
            );
        CreateClockIn createClockInThree =
            create(
                "businessId1",
                "employeeId1",
                "serviceId2",
                List.of(createRecord(TimeUnit.DAYS.toMillis(2), TimeRecordType.OUT, TimeRecordAction.REST)),
                List.of()
            );
        CreateClockIn createClockInFour =
            create(
                "businessId2",
                "employeeId1",
                "serviceId1",
                List.of(createRecord(2_500L, TimeRecordType.IN, TimeRecordAction.REST)),
                List.of()
            );

        // Same ids and day than createClockInOne
        ClockIn clockInOne =
            clockIn(
                clockInOneId,
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(clockInRecord(clockInOneId, 50_000L, TimeRecordType.IN, TimeRecordAction.REST)),
                List.of()
            );
        // Same ids and day than createClockInOne
        ClockIn clockInTwo =
            clockIn(
                clockInTwoId,
                "businessId1",
                "employeeId2",
                "serviceId1",
                List
                    .of(
                        clockInRecord(clockInTwoId, 50_000L, TimeRecordType.IN, TimeRecordAction.REST),
                        clockInRecord(clockInTwoId, 60_000L, TimeRecordType.OUT, TimeRecordAction.REST)
                    ),
                List.of()
            );
        // Different day than any createClockIn
        ClockIn clockInThree =
            clockIn(
                clockInThreeId,
                "businessId1",
                "employeeId1",
                "serviceId2",
                List
                    .of(
                        clockInRecord(clockInThreeId, TimeUnit.DAYS.toMillis(5), TimeRecordType.IN, TimeRecordAction.REST),
                        clockInRecord(clockInThreeId, TimeUnit.DAYS.toMillis(5) + 50_000L, TimeRecordType.OUT, TimeRecordAction.REST)
                    ),
                List.of()
            );

        UpdateClockIn updateClockInOne = toUpdate(clockInOne);
        UpdateClockIn updateClockInTwo = toUpdate(clockInTwo);

        Mockito
            .when(mapper.toUpdateClockIn(clockInOne, createClockInOne))
            .thenReturn(updateClockInOne);
        Mockito
            .when(mapper.toUpdateClockIn(clockInTwo, createClockInTwo))
            .thenReturn(updateClockInTwo);

        Tuple2<List<CreateClockIn>, List<UpdateClockIn>> result =
            processor
                .merge(
                    List.of(createClockInOne, createClockInTwo, createClockInThree, createClockInFour),
                    List.of(clockInOne, clockInTwo, clockInThree)
                );

        Assertions
            .assertThat(result.getT1())
            .containsExactlyInAnyOrder(createClockInThree, createClockInFour);
        Assertions
            .assertThat(result.getT2())
            .containsExactlyInAnyOrder(updateClockInOne, updateClockInTwo);

        Mockito
            .verify(mapper, Mockito.times(2))
            .toUpdateClockIn(Mockito.any(), Mockito.any());
    }

    @Test public void
    check_for_alerts() {
        UUID clockInOneId = UUID.randomUUID();
        UUID clockInTwoId = UUID.randomUUID();
        UUID alertOneId = UUID.randomUUID();
        UUID alertTwoId = UUID.randomUUID();
        CreateClockIn createClockInOne = create("businessId1", "employeeId1", "serviceId1", List.of(), new LinkedList<>());
        CreateClockIn createClockInTwo = create("businessId1", "employeeId2", "serviceId2", List.of(), new LinkedList<>());

        UpdateClockIn updateClockInOne = update(clockInOneId, List.of(), new LinkedList<>());
        UpdateClockIn updateClockInTwo = update(clockInTwoId, List.of(), new LinkedList<>());

        Alert alertOne = Mockito.mock(Alert.class);
        Alert alertTwo = Mockito.mock(Alert.class);

        Mockito
            .when(alertOne.id())
            .thenReturn(alertOneId);
        Mockito
            .when(alertOne.checkFor(createClockInOne))
            .thenReturn(true);
        Mockito
            .when(alertOne.checkFor(updateClockInOne))
            .thenReturn(true);
        Mockito
            .when(alertTwo.id())
            .thenReturn(alertTwoId);
        Mockito
            .when(alertTwo.checkFor(createClockInOne))
            .thenReturn(true);

        Tuple2<List<CreateClockIn>, List<UpdateClockIn>> result =
            processor
                .checkAlerts(
                    Tuples.of(
                        List.of(createClockInOne, createClockInTwo),
                        List.of(updateClockInOne, updateClockInTwo)
                    ),
                    List.of(alertOne, alertTwo)
                );

        Assertions
            .assertThat(result.getT1())
            .containsExactlyInAnyOrder(
                copyReplacingAlerts(createClockInOne, List.of(alertOneId, alertTwoId)),
                copyReplacingAlerts(createClockInTwo, List.of())
            );
        Assertions
            .assertThat(result.getT2())
            .containsExactlyInAnyOrder(
                copyReplacingAlerts(updateClockInOne, List.of(alertOneId)),
                copyReplacingAlerts(updateClockInTwo, List.of())
            );

        Mockito
            .verify(alertTwo, Mockito.times(2))
            .checkFor(Mockito.any(CreateClockIn.class));
        Mockito
            .verify(alertOne, Mockito.times(2))
            .checkFor(Mockito.any(UpdateClockIn.class));
        Mockito
            .verify(alertTwo, Mockito.times(2))
            .checkFor(Mockito.any(CreateClockIn.class));
        Mockito
            .verify(alertTwo, Mockito.times(2))
            .checkFor(Mockito.any(UpdateClockIn.class));
    }

    private ImportedClockIn imported(
        String businessId,
        long date,
        String employeeId,
        TimeRecordType type,
        String serviceId,
        TimeRecordAction action
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
        TimeRecordType type,
        TimeRecordAction action
    ) {
        return
            new CreateClockInRecord(
                date,
                type,
                action
            );
    }

    private ClockIn clockIn(
        UUID id,
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
        UUID clockInId,
        long date,
        TimeRecordType type,
        TimeRecordAction action
    ) {
        return
            new ClockInRecord(
                clockInId,
                date,
                type,
                action
            );
    }

    private UpdateClockIn update(
        UUID id,
        List<CreateClockInRecord> records,
        List<CreateClockInAlert> alerts
    ) {
        return
            new UpdateClockIn(
                id,
                records,
                alerts
            );
    }

    private CreateClockIn copyReplacingAlerts(CreateClockIn createClockIn, List<UUID> alerts) {
        return
            create(
                createClockIn.businessId(),
                createClockIn.employeeId(),
                createClockIn.serviceId(),
                createClockIn.records(),
                alerts.stream().map(CreateClockInAlert::new).toList()
            );
    }

    private UpdateClockIn copyReplacingAlerts(UpdateClockIn clockIn, List<UUID> alerts) {
        return
            new UpdateClockIn(
                clockIn.id(),
                clockIn.records(),
                alerts.stream().map(CreateClockInAlert::new).toList()
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
                    .toList(),
                clockIn.alerts().stream().map(alert -> new CreateClockInAlert(alert.alertId())).toList()
            );
    }
}