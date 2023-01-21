package orquest.domain.clockin.alert;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@ToString
public class ClockInAlert {

    private long id;
    private long clockInId;
    private UUID alertId;

    public ClockInAlert(long id, long clockInId, UUID alertId) {
        this.id = id;
        this.clockInId = clockInId;
        this.alertId = alertId;
    }

    public long id() {
        return id;
    }

    public long clockInId() {
        return clockInId;
    }

    public UUID alertId() {
        return alertId;
    }
}
