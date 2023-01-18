package orquest.infrastructure.repository.alert;

import orquest.domain.alert.Alert;
import orquest.domain.alert.AlertRepository;
import reactor.core.publisher.Flux;

public class JdbcAlertRepository implements AlertRepository {

    @Override
    public Flux<Alert> find() {
        throw new UnsupportedOperationException();
    }
}
