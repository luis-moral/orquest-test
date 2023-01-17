package orquest.infrastructure.handler.status.get;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

public class GetStatusHandler {

    public Mono<ServerResponse> status(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("OK"), String.class);
    }
}
