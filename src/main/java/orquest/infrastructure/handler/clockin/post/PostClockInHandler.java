package orquest.infrastructure.handler.clockin.post;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import orquest.domain.clockin.ImportClockInService;
import reactor.core.publisher.Mono;

import java.util.List;

public class PostClockInHandler {

    private final ImportClockInService importClockInService;
    private final PostClockInHandlerMapper mapper;

    public PostClockInHandler(
        ImportClockInService importClockInService,
        PostClockInHandlerMapper mapper
    ) {
        this.importClockInService = importClockInService;
        this.mapper = mapper;
    }

    public Mono<ServerResponse> process(ServerRequest request) {
        return
            ServerResponse
                .status(HttpStatus.CREATED)
                .body(
                    request
                        .bodyToMono(new ParameterizedTypeReference<List<PostClockInRequestItem>>() {})
                        .map(mapper::toImportClockIns)
                        .flatMap(importClockInService::process)
                        .then(),
                    Void.class
                );
    }
}
