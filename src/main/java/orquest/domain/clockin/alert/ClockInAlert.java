package orquest.domain.clockin.alert;

public class ClockInAlert {

    private long clockInId;
    private long alertId;

    public ClockInAlert(long clockInId, long alertId) {
        this.clockInId = clockInId;
        this.alertId = alertId;
    }

    public long clockInRecordId() {
        return clockInId;
    }

    public long alertId() {
        return alertId;
    }
}
