package orquest.domain.clockin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.alert.Alert;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.importer.ImportedClockIn;
import orquest.domain.clockin.importer.ImportedProcessor;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

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
        UpdateClockIn updateClockInOne = Mockito.mock(UpdateClockIn.class);
        UpdateClockIn updateClockInTwo = Mockito.mock(UpdateClockIn.class);

        Tuple2<List<CreateClockIn>, List<UpdateClockIn>> merged =
            Tuples.
                of(
                    List.of(createdOne, createdTwo),
                    List.of(updateClockInOne, updateClockInTwo)
                );

        Mockito.
            when(importedOne.businessId())
            .thenReturn("businessId1");

        Mockito
            .when(importedProcessor.filter(List.of(importedOne, importedTwo)))
            .thenReturn(filter);
        Mockito
            .when(importedProcessor.group(List.of(importedOne, importedTwo)))
            .thenReturn(List.of(createdOne, createdTwo));
        Mockito
            .when(importedProcessor.merge(List.of(createdOne, createdTwo), List.of(clockInOne, clockInTwo)))
            .thenReturn(merged);
        Mockito
            .when(importedProcessor.checkAlerts(merged, List.of(alertOne, alertTwo)))
            .thenReturn(merged);

        Mockito
            .when(alertRepository.find("businessId1"))
            .thenReturn(List.of(alertOne, alertTwo));
        Mockito
            .when(clockInRepository.find(filter))
            .thenReturn(List.of(clockInOne, clockInTwo));

        Mockito
            .when(clockInRepository.createAndUpdate(List.of(createdOne, createdTwo), List.of(updateClockInOne, updateClockInTwo)))
            .thenReturn(2L);

        StepVerifier
            .create(importClockInService.process(List.of(importedOne, importedTwo)))
            .expectNext(2L)
            .verifyComplete();

        Mockito
            .verify(importedProcessor, Mockito.times(1))
            .filter(Mockito.anyList());
        Mockito
            .verify(importedProcessor, Mockito.times(1))
            .group(Mockito.anyList());
        Mockito
            .verify(importedProcessor, Mockito.times(1))
            .merge(Mockito.anyList(), Mockito.anyList());
        Mockito
            .verify(importedProcessor, Mockito.times(1))
            .checkAlerts(Mockito.any(), Mockito.anyList());

        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .find(Mockito.any());
        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .createAndUpdate(Mockito.any(), Mockito.any());

        Mockito
            .verify(alertRepository, Mockito.times(1))
            .find(Mockito.any());
    }
}