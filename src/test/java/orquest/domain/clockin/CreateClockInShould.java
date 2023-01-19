package orquest.domain.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateClockInShould {

    @Test public void
    return_its_date() {
        CreateClockIn createClockIn =
            new CreateClockIn(
                "A",
                "B",
                "C",
                List.of(new CreateClockInRecord(TimeUnit.DAYS.toMillis(1), ClockInRecordType.IN, ClockInRecordAction.WORK)),
                List.of()
            );

        Assertions
            .assertThat(createClockIn.date())
            .hasValue(TimeUnit.DAYS.toMillis(1));
    }
}