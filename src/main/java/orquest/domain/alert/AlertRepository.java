package orquest.domain.alert;

import reactor.core.publisher.Flux;

public interface AlertRepository {

    Flux<Alert> find();
}
