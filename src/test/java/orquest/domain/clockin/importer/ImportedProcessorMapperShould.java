package orquest.domain.clockin.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

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
                ClockInRecordType.IN,
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
                ClockInRecordType.IN,
                "serviceId1",
                ClockInRecordAction.WORK
            );

        CreateClockInRecord expected =
            new CreateClockInRecord(
                1_000L,
                ClockInRecordType.IN,
                ClockInRecordAction.WORK
            );

        Assertions
            .assertThat(mapper.toCreateClockInRecord(importedClockIn))
            .isEqualTo(expected);
    }
}