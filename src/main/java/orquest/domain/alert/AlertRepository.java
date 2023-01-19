package orquest.domain.alert;

import java.util.List;

public interface AlertRepository {

    List<Alert> find(String businessId);
}
