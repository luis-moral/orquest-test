package orquest.infrastructure.util.generator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class IdGeneratorShould {

    private IdGenerator idGenerator;

    @BeforeEach
    public void setUp() {
        idGenerator = new IdGenerator();
    }

    @Test public void
    generate_different_ids_each_call() {
        UUID resultOne = idGenerator.generateId();
        UUID resultTwo = idGenerator.generateId();

        Assertions
            .assertThat(resultOne)
            .isNotEqualTo(resultTwo);
    }

}