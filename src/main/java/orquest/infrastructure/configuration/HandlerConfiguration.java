package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.domain.clockin.ImportClockInService;
import orquest.infrastructure.handler.clockin.post.PostClockInHandler;
import orquest.infrastructure.handler.clockin.post.PostClockInHandlerMapper;
import orquest.infrastructure.handler.status.get.GetStatusHandler;
import orquest.infrastructure.util.validator.RequestParameterValidator;

@Configuration
public class HandlerConfiguration {

    @Bean
    public GetStatusHandler getStatusHandler() {
        return new GetStatusHandler();
    }

    @Bean
    public PostClockInHandler postClockInHandler(
        ImportClockInService importClockInService,
        RequestParameterValidator requestParameterValidator
    ) {
        return new PostClockInHandler(importClockInService, new PostClockInHandlerMapper(requestParameterValidator));
    }
}
