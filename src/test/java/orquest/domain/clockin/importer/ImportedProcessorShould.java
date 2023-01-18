package orquest.domain.clockin.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.alert.Alert;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;

import java.util.List;
import java.util.Set;

public class ImportedProcessorShould {

    private ImportedProcessor processor;

    @BeforeEach
    public void setUp() {
        processor = new ImportedProcessor();
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
    process_imported_clock_ins_to_check_alerts_and_persist_the_data() {
        Alert alertOne = Mockito.mock(Alert.class);
        Alert alertTwo = Mockito.mock(Alert.class);

        processor.process(List.of(), List.of(), List.of(alertOne, alertTwo));
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
                date,
                employeeId,
                type,
                serviceId,
                action
            );
    }
}