package orquest.infrastructure.handler.clockin.post;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class PostClockInHandler {

    public Mono<ServerResponse> process(ServerRequest request) {
        throw new UnsupportedOperationException();
    }
}
