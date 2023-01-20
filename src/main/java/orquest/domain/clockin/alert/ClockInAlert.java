package orquest.domain.clockin.alert;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ClockInAlert {

    private long id;
    private long clockInId;
    private long alertId;

    public ClockInAlert(long id, long clockInId, long alertId) {
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

    public long alertId() {
        return alertId;
    }
}
