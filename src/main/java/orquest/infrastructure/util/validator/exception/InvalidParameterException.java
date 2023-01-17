package orquest.infrastructure.util.validator.exception;

public class InvalidParameterException extends ValidationException {

    public InvalidParameterException(String parameter, String expected) {
        super("Parameter [" + parameter + "] must be " + expected);
    }
}
