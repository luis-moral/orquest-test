package orquest.domain.clockin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class ImportClockInServiceShould {

    private ImportClockInService importClockInService;
    private ClockInRepository clockInRepository;

    @BeforeEach
    public void setUp() {
        clockInRepository = Mockito.mock(ClockInRepository.class);

        importClockInService = new ImportClockInService(clockInRepository);
    }

    @Test public void
    process_import_clock_ins_checking_alerts_and_persisting_them() {
        ImportedClockIn clockInOne = Mockito.mock(ImportedClockIn.class);
        ImportedClockIn clockInTwo = Mockito.mock(ImportedClockIn.class);

        Mockito
            .when(clockInRepository.create(List.of(clockInOne, clockInTwo)))
            .thenReturn(Mono.just(2L));

        StepVerifier
            .create(importClockInService.process(List.of(clockInOne, clockInTwo)))
            .expectNext(2L)
            .verifyComplete();

        Mockito
            .verify(clockInRepository, Mockito.times(1))
            .create(List.of(clockInOne, clockInTwo));
    }
}