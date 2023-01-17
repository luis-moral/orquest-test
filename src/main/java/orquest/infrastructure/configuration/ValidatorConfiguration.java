package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.infrastructure.util.validator.RequestParameterValidator;

@Configuration
public class ValidatorConfiguration {

    @Bean
    public RequestParameterValidator requestParameterValidator() {
        return new RequestParameterValidator();
    }
}
