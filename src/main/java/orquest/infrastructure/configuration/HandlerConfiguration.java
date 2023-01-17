package orquest.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import orquest.infrastructure.handler.clockin.post.PostClockInHandler;
import orquest.infrastructure.handler.status.get.GetStatusHandler;

@Configuration
public class HandlerConfiguration {

    @Bean
    public GetStatusHandler getStatusHandler() {
        return new GetStatusHandler();
    }

    @Bean
    public PostClockInHandler postClockInHandler() {
        return new PostClockInHandler();
    }
}
