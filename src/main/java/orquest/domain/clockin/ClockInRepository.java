package orquest.domain.clockin;

import reactor.util.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public interface ClockInRepository {

    ClockIn find(String businessId, long id);

    default List<ClockIn> find() {
        return find(null);
    }

    List<ClockIn> find(@Nullable ClockInFilter filter);

    Long update(List<ClockIn> clockIns);

    Long create(List<CreateClockIn> clockIns);

    Long createAndUpdate(Collection<CreateClockIn> newClockIns, Collection<UpdateClockIn> updatedClockIns);
}
