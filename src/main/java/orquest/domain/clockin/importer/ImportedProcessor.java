package orquest.domain.clockin.importer;

import orquest.domain.alert.Alert;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import reactor.core.publisher.Flux;

import java.util.List;

public class ImportedProcessor {

    public ClockInFilter filter(List<ImportedClockIn> clockIns) {
        ImportedProcessorFields fields =
            clockIns
                .stream()
                .collect(
                    ImportedProcessorFields::new,
                    (filterFields, clockIn) -> {
                        filterFields.from(clockIn.date());
                        filterFields.to(clockIn.date());
                        filterFields.businessId(clockIn.businessId());
                        filterFields.employeeId(clockIn.employeeId());
                    },
                    ImportedProcessorFields::combine
                );

        return
            new ClockInFilter.Builder()
                .from(fields.from())
                .to(fields.to())
                .businessIds(fields.businessIds())
                .employeeIds(fields.employeeIds())
                .build();
    }

    public Flux<CreateClockIn> process(
        List<ImportedClockIn> clockIns,
        List<ClockIn> currentClockIns,
        List<Alert> alerts
    ) {
        throw new UnsupportedOperationException();
    }
}
