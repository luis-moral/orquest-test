package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.ImportClockInService;
import orquest.domain.clockin.importer.ImportedProcessor;
import orquest.domain.clockin.importer.ImportedProcessorMapper;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ImportClockInService importClockInService(
        AlertRepository alertRepository,
        ClockInRepository clockInRepository
    ) {
        return
            new ImportClockInService(
                alertRepository, clockInRepository,
                new ImportedProcessor(new ImportedProcessorMapper())
            );
    }
}
