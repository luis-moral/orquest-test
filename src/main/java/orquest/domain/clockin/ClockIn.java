package orquest.domain.clockin;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.time.TimeUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@ToString
public class ClockIn {

    private final long id;
    private final String businessId;
    private final String employeeId;
    private final String serviceId;
    private final List<ClockInRecord> records;
    private final List<ClockInAlert> alerts;

    public ClockIn(
        long id,
        String businessId,
        String employeeId,
        String serviceId
    ) {
        this(id, businessId, employeeId, serviceId, new LinkedList<>(), new LinkedList<>());
    }

    public ClockIn(
        long id,
        String businessId,
        String employeeId,
        String serviceId,
        List<ClockInRecord> records,
        List<ClockInAlert> alerts
    ) {
        this.id = id;
        this.businessId = businessId;
        this.employeeId = employeeId;
        this.serviceId = serviceId;
        this.records = records;
        this.alerts = alerts;
    }

    public long id() {
        return id;
    }

    public String businessId() {
        return businessId;
    }

    public String employeeId() {
        return employeeId;
    }

    public String serviceId() {
        return serviceId;
    }

    public List<ClockInRecord> records() {
        return records;
    }

    public List<ClockInAlert> alerts() {
        return alerts;
    }

    public Optional<Long> date() {
        return TimeUtils.clockInDay(records);
    }
}
