package orquest.domain.clockin;

import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;

import java.util.List;

public record CreateClockIn(
    String businessId,
    String employeeId,
    String serviceId,
    List<ClockInRecord> records,
    List<ClockInAlert> alerts
) {}
