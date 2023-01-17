package orquest.domain.clockin;

import java.time.ZonedDateTime;

public class ClockInRecord {

    private final String businessId;
    private final ZonedDateTime date;
    private final String employeeId;
    private final ClockInActionType action;
    private final String serviceId;
    private final ClockInRecordType type;

    public ClockInRecord(
        String businessId,
        ZonedDateTime date,
        String employeeId,
        ClockInActionType action,
        String serviceId,
        ClockInRecordType type
    ) {
        this.businessId = businessId;
        this.date = date;
        this.employeeId = employeeId;
        this.action = action;
        this.serviceId = serviceId;
        this.type = type;
    }

    public String businessId() {
        return businessId;
    }

    public ZonedDateTime date() {
        return date;
    }

    public String employeeId() {
        return employeeId;
    }

    public ClockInActionType action() {
        return action;
    }

    public String serviceId() {
        return serviceId;
    }

    public ClockInRecordType type() {
        return type;
    }
}
