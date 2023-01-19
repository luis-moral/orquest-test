package orquest.domain.clockin;

import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public record CreateClockIn(
    String businessId,
    String employeeId,
    String serviceId,
    List<CreateClockInRecord> records,
    List<CreateClockInAlert> alerts
) {
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
