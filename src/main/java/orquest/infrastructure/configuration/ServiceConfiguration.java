package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.ImportClockInService;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ImportClockInService importClockInService(
        ClockInRepository clockInRepository
    ) {
        return new ImportClockInService(clockInRepository);
    }
}
