package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.domain.alert.AlertRepository;
import orquest.domain.clockin.ClockInRepository;
import orquest.domain.clockin.ImportClockInService;
import orquest.domain.clockin.importer.ImportedProcessor;
import orquest.domain.clockin.importer.ImportedProcessorMapper;
import orquest.domain.clockin_employee.GetEmployeeClockInService;
import orquest.domain.clockin_employee.GetEmployeeClockInServiceMapper;

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

    @Bean
    public GetEmployeeClockInService getEmployeeClockInService(
        ClockInRepository clockInRepository
    ) {
        return
            new GetEmployeeClockInService(
                clockInRepository,
                new GetEmployeeClockInServiceMapper()
            );
    }
}
