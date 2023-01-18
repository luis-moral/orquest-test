package orquest.domain.clockin.importer;

import org.springframework.lang.Nullable;
import orquest.domain.alert.Alert;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import reactor.core.publisher.Flux;

import java.util.List;

public class ImportedProcessor {

    public ClockInFilter filter(List<ImportedClockIn> clockIns) {
        throw new UnsupportedOperationException();
    }

    public Flux<CreateClockIn> process(
        List<ImportedClockIn> clockIns,
        @Nullable List<ClockIn> currentClockIns,
        @Nullable List<Alert> alerts
    ) {
        throw new UnsupportedOperationException();
    }
}
