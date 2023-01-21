package orquest.domain.clockin.alert;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@ToString
public class ClockInAlert {

    private UUID clockInId;
    private UUID alertId;

    public ClockInAlert(UUID clockInId, UUID alertId) {
        this.clockInId = clockInId;
        this.alertId = alertId;
    }

    public UUID clockInId() {
        return clockInId;
    }

    public UUID alertId() {
        return alertId;
    }
}
