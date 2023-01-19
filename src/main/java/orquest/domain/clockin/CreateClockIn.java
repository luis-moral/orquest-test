package orquest.domain.clockin;

import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.util.List;

public record CreateClockIn(
    String businessId,
    String employeeId,
    String serviceId,
    List<CreateClockInRecord> records,
    List<CreateClockInAlert> alerts
) {}
