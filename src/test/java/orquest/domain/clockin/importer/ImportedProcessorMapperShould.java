package orquest.domain.clockin.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.CreateClockInRecord;
import orquest.domain.time.TimeRecordType;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class ImportedProcessorMapperShould {

    private ImportedProcessorMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ImportedProcessorMapper();
    }

    @Test public void
    map_imported_clock_in_to_create_clock_in() {
        ImportedClockIn importedClockIn =
            new ImportedClockIn(
                "businessId1",
                Instant.ofEpochMilli(1_000L).atZone(ZoneOffset.UTC),
                "employeeId1",
                TimeRecordType.IN,
                "serviceId1",
                ClockInRecordAction.WORK
            );

        CreateClockIn expected =
            new CreateClockIn(
                "businessId1",
                "employeeId1",
                "serviceId1",
                List.of(),
                List.of()
            );

        Assertions
            .assertThat(mapper.toCreateClockIn(importedClockIn))
            .isEqualTo(expected);
    }

    @Test public void
    map_imported_clock_in_to_create_clock_in_record() {
        ImportedClockIn importedClockIn =
            new ImportedClockIn(
                "businessId1",
                Instant.ofEpochMilli(1_000L).atZone(ZoneOffset.UTC),
                "employeeId1",
                TimeRecordType.IN,
                "serviceId1",
                ClockInRecordAction.WORK
            );

        CreateClockInRecord expected =
            new CreateClockInRecord(
                1_000L,
                TimeRecordType.IN,
                ClockInRecordAction.WORK
            );

        Assertions
            .assertThat(mapper.toCreateClockInRecord(importedClockIn))
            .isEqualTo(expected);
    }

    @Test public void
    map_clock_in_and_create_clock_in_to_update_clock_in() {
        UUID clockInId = UUID.randomUUID();

        ClockIn clockIn =
            new ClockIn(
                clockInId,
                "A",
                "B",
                "C",
                List.of(
                    new ClockInRecord(
                        clockInId,
                        1_000L,
                        TimeRecordType.IN,
                        ClockInRecordAction.WORK
                    )
                ),
                List.of()
        );
        CreateClockIn createClockIn =
            new CreateClockIn(
                "A",
                "B",
                "C",
                List.of(
                    new CreateClockInRecord(
                        1_500L,
                        TimeRecordType.OUT,
                        ClockInRecordAction.WORK
                    )
                ),
                List.of()
            );

        UpdateClockIn expected =
            new UpdateClockIn(
                clockInId,
                List.of(
                    new CreateClockInRecord(
                        1_000L,
                        TimeRecordType.IN,
                        ClockInRecordAction.WORK
                    ),
                    new CreateClockInRecord(
                        1_500L,
                        TimeRecordType.OUT,
                        ClockInRecordAction.WORK
                    )
                ),
                List.of()
            );

        Assertions
            .assertThat(mapper.toUpdateClockIn(clockIn, createClockIn))
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test public void
    map_clock_in_record_to_create_clock_in_record() {
        UUID clockInId = UUID.randomUUID();

        ClockInRecord clockInRecord =
            new ClockInRecord(
                clockInId,
                1_000L,
                TimeRecordType.IN,
                ClockInRecordAction.WORK
            );

        CreateClockInRecord expected =
            new CreateClockInRecord(
                1_000L,
                TimeRecordType.IN,
                ClockInRecordAction.WORK
            );

        Assertions
            .assertThat(mapper.toCreateClockInRecord(clockInRecord))
            .isEqualTo(expected);
    }
}