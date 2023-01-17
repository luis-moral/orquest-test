package orquest.domain.clockin;

import java.util.List;

public interface ClockInRepository {

    List<ClockInRecord> getByEmployee(String id);
}
