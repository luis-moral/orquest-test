package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.domain.clockin.CreateEmployeeClockInService;

@Configuration
public class ServiceConfiguration {

    @Bean
    public CreateEmployeeClockInService createEmployeeClockInService() {
        return new CreateEmployeeClockInService();
    }
}
