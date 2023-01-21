package orquest.domain.time;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class MatchedTimeRecordShould {

    private MatchedTimeRecord matchedTimeRecord;

    @BeforeEach
    public void setUp() {
        matchedTimeRecord = new MatchedTimeRecord();
    }

    @Test public void
    return_if_matched_when_both_elements_exists() {
        Assertions
            .assertThat(matchedTimeRecord.isUnmatched())
            .isTrue();

        matchedTimeRecord.in(Mockito.mock(TimeRecord.class));
        Assertions
            .assertThat(matchedTimeRecord.isUnmatched())
            .isTrue();

        matchedTimeRecord.out(Mockito.mock(TimeRecord.class));
        Assertions
            .assertThat(matchedTimeRecord.isMatched())
            .isTrue();
    }

    @Test public void
    return_if_the_elements_have_an_action() {
        TimeRecord timeRecord = Mockito.mock(TimeRecord.class);

        Mockito
            .when(timeRecord.action())
            .thenReturn(TimeRecordAction.REST);

        Assertions
            .assertThat(matchedTimeRecord.hasAction(TimeRecordAction.WORK))
            .isFalse();

        matchedTimeRecord.in(timeRecord);
        Assertions
            .assertThat(matchedTimeRecord.hasAction(TimeRecordAction.WORK))
            .isFalse();

        Assertions
            .assertThat(matchedTimeRecord.hasAction(TimeRecordAction.REST))
            .isTrue();

        matchedTimeRecord.in(null);
        matchedTimeRecord.out(timeRecord);
        Assertions
            .assertThat(matchedTimeRecord.hasAction(TimeRecordAction.REST))
            .isTrue();
    }

    @Test public void
    return_if_missing_by_type() {
        TimeRecord timeRecord = Mockito.mock(TimeRecord.class);

        Assertions
            .assertThat(matchedTimeRecord.isMissing(TimeRecordType.IN))
            .isTrue();
        Assertions
            .assertThat(matchedTimeRecord.isMissing(TimeRecordType.OUT))
            .isTrue();

        matchedTimeRecord.out(timeRecord);
        Assertions
            .assertThat(matchedTimeRecord.isMissing(TimeRecordType.IN))
            .isTrue();
        Assertions
            .assertThat(matchedTimeRecord.isMissing(TimeRecordType.OUT))
            .isFalse();
    }

    @Test public void
    set_by_type() {
        TimeRecord timeRecord = Mockito.mock(TimeRecord.class);

        Mockito
            .when(timeRecord.type())
            .thenReturn(TimeRecordType.OUT);

        matchedTimeRecord.setByType(timeRecord);

        Assertions
            .assertThat(matchedTimeRecord.in())
            .isEmpty();
        Assertions
            .assertThat(matchedTimeRecord.out())
            .isPresent();
    }

    @Test public void
    calculate_date_difference() {
        TimeRecord in = Mockito.mock(TimeRecord.class);
        TimeRecord out = Mockito.mock(TimeRecord.class);

        Mockito
            .when(in.date())
            .thenReturn(2_500L);
        Mockito
            .when(out.date())
            .thenReturn(10_500L);

        Assertions
            .assertThat(matchedTimeRecord.dateDifference())
            .isEmpty();

        matchedTimeRecord.in(in);
        matchedTimeRecord.out(out);
        Assertions
            .assertThat(matchedTimeRecord.dateDifference())
            .hasValue(8_000L);
    }
}