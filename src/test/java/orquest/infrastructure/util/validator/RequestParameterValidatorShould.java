package orquest.infrastructure.util.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.infrastructure.util.validator.exception.InvalidParameterException;
import orquest.infrastructure.util.validator.exception.MandatoryParameterException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class RequestParameterValidatorShould {

    private RequestParameterValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new RequestParameterValidator();
    }

    @Test public void
    validate_mandatory_string() {
        Optional<String> valid = Optional.of("something");
        Optional<String> empty = Optional.empty();

        Assertions
            .assertThat(validator.mandatoryString(valid, "someParameter"))
            .isEqualTo("something");

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryString(empty, "someParameter"))
            .isInstanceOf(MandatoryParameterException.class)
            .hasMessage("Parameter [someParameter] is mandatory");
    }

    @Test public void
    validate_mandatory_clock_in_record_type() {
        Optional<String> validOne = Optional.of("IN");
        Optional<String> validTwo = Optional.of("out");
        Optional<String> invalid = Optional.of("NAN");
        Optional<String> empty = Optional.empty();

        Assertions
            .assertThat(validator.mandatoryClockInRecordType(validOne, "someParameter"))
            .isEqualTo(ClockInRecordType.IN);
        Assertions
            .assertThat(validator.mandatoryClockInRecordType(validTwo, "someParameter"))
            .isEqualTo(ClockInRecordType.OUT);

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryClockInRecordType(invalid, "someParameter"))
            .isInstanceOf(InvalidParameterException.class)
            .hasMessage("Parameter [someParameter] must be a valid record action");

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryClockInRecordType(empty, "someParameter"))
            .isInstanceOf(MandatoryParameterException.class)
            .hasMessage("Parameter [someParameter] is mandatory");

    }

    @Test public void
    validate_mandatory_clock_in_record_action() {
        Optional<String> validOne = Optional.of("WORK");
        Optional<String> validTwo = Optional.of("rest");
        Optional<String> invalid = Optional.of("NAN");
        Optional<String> empty = Optional.empty();

        Assertions
            .assertThat(validator.mandatoryClockInRecordAction(validOne, "someParameter"))
            .isEqualTo(ClockInRecordAction.WORK);
        Assertions
            .assertThat(validator.mandatoryClockInRecordAction(validTwo, "someParameter"))
            .isEqualTo(ClockInRecordAction.REST);

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryClockInRecordAction(invalid, "someParameter"))
            .isInstanceOf(InvalidParameterException.class)
            .hasMessage("Parameter [someParameter] must be a valid action");

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryClockInRecordAction(empty, "someParameter"))
            .isInstanceOf(MandatoryParameterException.class)
            .hasMessage("Parameter [someParameter] is mandatory");
    }

    @Test public void
    validate_mandatory_iso_8601_date() {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1668853977000L), ZoneId.of("CET"));

        Optional<String> valid = Optional.of(date.toOffsetDateTime().toString());
        Optional<String> invalid = Optional.of("Date");
        Optional<String> empty = Optional.empty();

        Assertions
            .assertThat(validator.mandatoryDate(valid, "date").toInstant().toEpochMilli())
            .isEqualTo(1668853977000L);

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryDate(invalid, "date"))
            .isInstanceOf(InvalidParameterException.class)
            .hasMessage("Parameter [date] must be a valid ISO-8601 date");

        Assertions
            .assertThatThrownBy(() -> validator.mandatoryDate(empty, "date"))
            .isInstanceOf(MandatoryParameterException.class)
            .hasMessage("Parameter [date] is mandatory");
    }
}