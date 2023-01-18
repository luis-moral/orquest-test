package orquest.infrastructure.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import orquest.infrastructure.util.validator.exception.ValidationException;

import java.util.Map;

@Component
public class GlobalErrorMapper extends DefaultErrorAttributes {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(request, options);
        Throwable exception = getError(request);

        format(exception, map);
        log(exception);

        return map;
    }

    private void format(Throwable exception, Map<String, Object> map) {
        if (exception instanceof ResponseStatusException) {
            map.put("error", ((ResponseStatusException) exception).getReason());
        }
        else if (exception instanceof ValidationException) {
            map.put("status", HttpStatus.BAD_REQUEST.value());
            map.put("error", exception.getMessage());
        }
        else {
            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("error", "Internal Server Error");
        }
    }

    private void log(Throwable exception) {
        if (exception instanceof ResponseStatusException) {
            logger.error(exception.getLocalizedMessage());
        }
        else {
            logger.error(exception.getLocalizedMessage(), exception);
        }
    }
}
