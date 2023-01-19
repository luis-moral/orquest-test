package orquest.domain.clockin;

import orquest.domain.clockin.alert.ClockInAlert;
import orquest.domain.clockin.record.ClockInRecord;

import java.util.List;

public record UpdateClockIn(
    long id,
    String businessId,
    List<ClockInRecord> records,
    List<ClockInAlert> alerts
) {}
