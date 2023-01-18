package orquest.domain.clockin.alert;

public class ClockInAlert {

    private long clockInRecordId;
    private long alertId;

    public ClockInAlert(long clockInRecordId, long alertId) {
        this.clockInRecordId = clockInRecordId;
        this.alertId = alertId;
    }

    public long clockInRecordId() {
        return clockInRecordId;
    }

    public long alertId() {
        return alertId;
    }
}
