package orquest.domain.clockin;

import reactor.core.publisher.Mono;

import java.util.List;

public class ImportClockInService {

    private final ClockInRepository clockInRepository;

    public ImportClockInService(ClockInRepository clockInRepository) {
        this.clockInRepository = clockInRepository;
    }

    public Mono<Long> process(List<ImportedClockIn> clockIns) {
        return
            clockInRepository
                .create(clockIns);
    }
}
