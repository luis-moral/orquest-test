package orquest.domain.clockin;

import reactor.util.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public interface ClockInRepository {

    default List<ClockIn> find() {
        return find(null);
    }

    List<ClockIn> find(@Nullable ClockInFilter filter);

    Long create(List<CreateClockIn> clockIns);

    Long createAndUpdate(Collection<CreateClockIn> newClockIns, Collection<UpdateClockIn> updatedClockIns);
}
