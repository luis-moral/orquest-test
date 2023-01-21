package orquest.domain.clockin;

import reactor.util.annotation.Nullable;

import java.util.Collection;
import java.util.List;

public interface ClockInRepository {

    default List<ClockIn> find() {
        return find(null);
    }

    List<ClockIn> find(@Nullable ClockInFilter filter);

    int create(List<CreateClockIn> clockIns);

    int update(List<UpdateClockIn> clockIns);

    int createAndUpdate(Collection<CreateClockIn> newClockIns, Collection<UpdateClockIn> updatedClockIns);
}
