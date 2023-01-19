package orquest.infrastructure.repository.alert;

import orquest.domain.alert.Alert;
import orquest.domain.alert.AlertRepository;

import java.util.List;

public class JdbcAlertRepository implements AlertRepository {

    @Override
    public List<Alert> find(String businessId) {
        throw new UnsupportedOperationException();
    }
}
