package orquest.domain.clockin;

import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.importer.ImportedClockIn;
import orquest.domain.clockin.importer.ImportedProcessor;
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
            Mono
                // Calls importedProcessor.group() and clockInRepository.find() simultaneously and waits till both methods are completed
                .zip(
                    Mono.fromCallable(() -> importedProcessor.group(importedClockIns)),
                    Mono.fromCallable(() -> clockInRepository.find(importedProcessor.filter(importedClockIns)))
                )
                .map(createdAndCurrent -> importedProcessor.merge(createdAndCurrent.getT1(), createdAndCurrent.getT2()))
                .map(createAndUpdate -> importedProcessor.checkAlerts(createAndUpdate, alertRepository.find(importedClockIns.get(0).businessId())))
                .map(createAndUpdate -> clockInRepository.createAndUpdate(createAndUpdate.getT1(), createAndUpdate.getT2()));
    }
}
