package orquest.domain.clockin;

import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

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

    public long date() {
        return
            records
                .stream()
                .findAny()
                .map(
                    record ->
                        Instant
                            .ofEpochMilli(record.date())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli()
                )
                .orElse(Long.MIN_VALUE);
    }
}
