package orquest.domain.clockin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.alert.Alert;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.importer.ImportedClockIn;
import orquest.domain.clockin.importer.ImportedProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class ImportClockInServiceShould {

    private AlertRepository alertRepository;
    private ClockInRepository clockInRepository;
    private ImportedProcessor importedProcessor;
    private ImportClockInService importClockInService;

    @BeforeEach
    public void setUp() {
        alertRepository = Mockito.mock(AlertRepository.class);
        clockInRepository = Mockito.mock(ClockInRepository.class);
        importedProcessor = Mockito.mock(ImportedProcessor.class);

        importClockInService = new ImportClockInService(alertRepository, clockInRepository, importedProcessor);
    }

    @Test public void
    process_import_clock_ins_checking_alerts_and_persisting_them() {
        ClockInFilter filter = Mockito.mock(ClockInFilter.class);
        Alert alertOne = Mockito.mock(Alert.class);
        Alert alertTwo = Mockito.mock(Alert.class);
        ClockIn clockInOne = Mockito.mock(ClockIn.class);
        ClockIn clockInTwo = Mockito.mock(ClockIn.class);
        ImportedClockIn importedOne = Mockito.mock(ImportedClockIn.class);
        ImportedClockIn importedTwo = Mockito.mock(ImportedClockIn.class);
        CreateClockIn createdOne = Mockito.mock(CreateClockIn.class);
        CreateClockIn createdTwo = Mockito.mock(CreateClockIn.class);

        Mockito
            .when(importedProcessor.filter(List.of(importedOne, importedTwo)))
            .thenReturn(filter);

        Mockito
            .when(alertRepository.find())
            .thenReturn(Flux.just(alertOne, alertTwo));

        Mockito
            .when(clockInRepository.find(filter))
            .thenReturn(Flux.just(clockInOne, clockInTwo));

        Mockito
            .when(importedProcessor.process(List.of(importedOne, importedTwo), List.of(clockInOne, clockInTwo), List.of(alertOne, alertTwo)))
            .thenReturn(Flux.just(createdOne, createdTwo));

        Mockito
            .when(clockInRepository.create(List.of(createdOne, createdTwo)))
            .thenReturn(Mono.just(2L));

        StepVerifier
            .create(importClockInService.process(List.of(importedOne, importedTwo)))
            .expectNext(2L)
            .verifyComplete();

        Mockito
            .verify(importedProcessor, Mockito.times(1))
            .filter(Mockito.anyList());

        Mockito
            .verify(alertRepository, Mockito.times(1))
            .find();

        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .find(Mockito.any());

        Mockito
            .verify(importedProcessor, Mockito.times(1))
            .process(Mockito.anyList(), Mockito.anyList(), Mockito.anyList());

        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .create(Mockito.anyList());
    }
}