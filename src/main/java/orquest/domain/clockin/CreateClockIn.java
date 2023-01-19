package orquest.domain.clockin;

import orquest.domain.clockin.alert.CreateClockInAlert;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public record CreateClockIn(
    String businessId,
    String employeeId,
    String serviceId,
    List<CreateClockInRecord> records,
    List<CreateClockInAlert> alerts
) {
    public Optional<Long> date() {
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
                );
    }
}
