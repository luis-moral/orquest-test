package orquest.infrastructure.util.validator;

import orquest.domain.clockin.ClockInActionType;
import orquest.domain.clockin.ClockInRecordType;
import orquest.infrastructure.util.validator.exception.InvalidParameterException;
import orquest.infrastructure.util.validator.exception.MandatoryParameterException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class RequestParameterValidator {

    public String mandatoryString(Optional<String> optionalValue, String parameterName) {
        return optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));
    }

    public ClockInActionType mandatoryClockInActionType(Optional<String> optionalValue, String parameterName) {
        String value = optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));

        return validateInActionType(value, parameterName, "a valid action type");
    }

    public ClockInRecordType mandatoryClockInRecordType(Optional<String> optionalValue, String parameterName) {
        String value = optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));

        return validateClockInRecordType(value, parameterName, "a valid record type");
    }

    public long mandatoryDate(Optional<String> optionalValue, String parameterName) {
        String value = optionalValue.orElseThrow(() -> new MandatoryParameterException(parameterName));

        return validateDate(value, parameterName, "a valid ISO-8601 date");
    }

    private ClockInActionType validateInActionType(String value, String errorParameter, String errorExpected) {
        try {
            return ClockInActionType.valueOf(value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidParameterException(errorParameter, errorExpected);
        }
    }

    private ClockInRecordType validateClockInRecordType(String value, String errorParameter, String errorExpected) {
        try {
            return ClockInRecordType.valueOf(value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidParameterException(errorParameter, errorExpected);
        }
    }

    private long validateDate(String value, String errorParameter, String errorExpected) {
        try {
            return ZonedDateTime.parse(value).toInstant().toEpochMilli();
        }
        catch (DateTimeParseException e) {
            throw new InvalidParameterException(errorParameter, errorExpected);
        }
    }
}
