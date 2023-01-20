package orquest.infrastructure.util.validator;

import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.time.TimeRecordType;
import orquest.infrastructure.util.validator.exception.InvalidParameterException;
import orquest.infrastructure.util.validator.exception.MandatoryParameterException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class RequestParameterValidator {

    public String mandatoryString(Optional<String> optionalValue, String parameterName) {
        return optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));
    }

    public TimeRecordType mandatoryClockInRecordType(Optional<String> optionalValue, String parameterName) {
        String value = optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));

        return validateClockInRecordType(value, parameterName, "a valid record action");
    }

    public ClockInRecordAction mandatoryClockInRecordAction(Optional<String> optionalValue, String parameterName) {
        String value = optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));

        return validateClockInRecordAction(value, parameterName, "a valid action");
    }

    public ZonedDateTime mandatoryDate(Optional<String> optionalValue, String parameterName) {
        String value = optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));

        return validateDate(value, parameterName, "a valid ISO-8601 date");
    }

    private TimeRecordType validateClockInRecordType(String value, String errorParameter, String errorExpected) {
        try {
            return TimeRecordType.valueOf(value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidParameterException(errorParameter, errorExpected);
        }
    }

    private ClockInRecordAction validateClockInRecordAction(String value, String errorParameter, String errorExpected) {
        try {
            return ClockInRecordAction.valueOf(value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidParameterException(errorParameter, errorExpected);
        }
    }

    private ZonedDateTime validateDate(String value, String errorParameter, String errorExpected) {
        try {
            return ZonedDateTime.parse(value);
        }
        catch (DateTimeParseException e) {
            throw new InvalidParameterException(errorParameter, errorExpected);
        }
    }
}
