package orquest.domain.clockin_employee;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.ClockInFilter;

import java.util.Set;

public class GetEmployeeClockInServiceMapperShould {
    
    private GetEmployeeClockInServiceMapper mapper;
    
    @BeforeEach
    public void setUp() {
        mapper = new GetEmployeeClockInServiceMapper();
    }
    
    @Test public void 
    map_employee_id_to_clock_in_filter() {
        ClockInFilter expected = new ClockInFilter.Builder().employeeIds(Set.of("employeeId1")).build();
        
        Assertions
            .assertThat(mapper.toFilter("employeeId1"))
            .isEqualTo(expected);
    }
}