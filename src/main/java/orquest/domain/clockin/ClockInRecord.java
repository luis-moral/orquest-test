package orquest.domain.clockin;

public class ClockInRecord {

    private final String businessId;
    private final long date;
    private final String employeeId;
    private final ClockInRecordType recordType;
    private final String serviceId;
    private final ClockInType type;

    public ClockInRecord(
        String businessId,
        long date,
        String employeeId,
        ClockInRecordType recordType,
        String serviceId,
        ClockInType type
    ) {
        this.businessId = businessId;
        this.date = date;
        this.employeeId = employeeId;
        this.recordType = recordType;
        this.serviceId = serviceId;
        this.type = type;
    }

    public String businessId() {
        return businessId;
    }

    public long date() {
        return date;
    }

    public String employeeId() {
        return employeeId;
    }

    public ClockInRecordType recordType() {
        return recordType;
    }

    public String serviceId() {
        return serviceId;
    }

    public ClockInType type() {
        return type;
    }
}
