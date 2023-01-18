package orquest.domain.clockin.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ImportedProcessorFieldsShould {
    
    private ImportedProcessorFields fields;
    
    @BeforeEach
    public void setUp() {
        fields = new ImportedProcessorFields();
    }

    @Test public void
    only_set_from_date_if_less_than_current() {
        fields.from(1_500L);

        fields.from(1_000L);
        Assertions
            .assertThat(fields.from())
            .isEqualTo(1_000L);

        fields.from(2_500L);
        Assertions
            .assertThat(fields.from())
            .isEqualTo(1_000L);
    }

    @Test public void
    only_set_to_date_if_greater_than_current() {
        fields.to(1_500L);

        fields.to(1_000L);
        Assertions
            .assertThat(fields.to())
            .isEqualTo(1_500L);

        fields.to(2_500L);
        Assertions
            .assertThat(fields.to())
            .isEqualTo(2_500L);
    }

    @Test public void
    correctly_combines_with_other_fields() {
        ImportedProcessorFields anotherFields = new ImportedProcessorFields();

        fields.from(1_000L);
        fields.to(1_500L);
        fields.businessId("A1");
        fields.employeeId("B1");

        anotherFields.from(500L);
        anotherFields.to(1_000L);
        anotherFields.businessId("A2");
        anotherFields.employeeId("B2");

        fields.combine(anotherFields);

        Assertions
            .assertThat(fields.from())
            .isEqualTo(500L);
        Assertions
            .assertThat(fields.to())
            .isEqualTo(1_500L);
        Assertions
            .assertThat(fields.businessIds())
            .isEqualTo(Set.of("A1", "A2"));
        Assertions
            .assertThat(fields.employeeIds())
            .isEqualTo(Set.of("B1", "B2"));
    }
}