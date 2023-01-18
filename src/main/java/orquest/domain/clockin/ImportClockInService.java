package orquest.domain.clockin;

import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.importer.ImportedProcessor;
import orquest.domain.clockin.importer.ImportedClockIn;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ImportClockInService {

    private final AlertRepository alertRepository;
    private final ClockInRepository clockInRepository;
    private final ImportedProcessor importedProcessor;

    public ImportClockInService(
        AlertRepository alertRepository,
        ClockInRepository clockInRepository,
        ImportedProcessor importedProcessor
    ) {
        this.alertRepository = alertRepository;
        this.clockInRepository = clockInRepository;
        this.importedProcessor = importedProcessor;
    }

    public Mono<Long> process(List<ImportedClockIn> importedClockIns) {
        return
            Flux
                // Calls clockInRepository.find() and alertRepository.find() concurrently and waits till both methods are completed
                .zip(
                    clockInRepository.find(importedProcessor.filter(importedClockIns)).collectList(),
                    alertRepository.find().collectList()
                )
                // currentClockInsAndAlerts is a Tuple2 of the results of the previous calls -> Tuple2<List<ClockIn>, List<Alert>>
                .flatMap(
                    currentClockInsAndAlerts ->
                        importedProcessor
                            .process(importedClockIns, currentClockInsAndAlerts.getT1(), currentClockInsAndAlerts.getT2())
                )
                // Flatmap returns a Flux of elements, since our database driver is not reactive, collectList waits till all results are emitted and collects them in a list
                .collectList()
                .flatMap(clockInRepository::create);
    }
}
