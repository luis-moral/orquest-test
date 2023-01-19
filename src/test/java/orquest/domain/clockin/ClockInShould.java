package orquest.domain.clockin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClockInShould {

    @Test public void
    return_its_date() {
        ClockIn clockIn =
            new ClockIn(
                1L,
                "A",
                "B",
                "C",
                List.of(new ClockInRecord(1L, 1L, TimeUnit.DAYS.toMillis(1), ClockInRecordType.IN, ClockInRecordAction.WORK)),
                List.of()
            );

        Assertions
            .assertThat(clockIn.date())
            .isEqualTo(TimeUnit.DAYS.toMillis(1));
    }
}