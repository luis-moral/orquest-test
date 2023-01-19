package orquest.domain.clockin.importer;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;
import orquest.domain.clockin.record.ClockInRecord;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ImportedProcessorMapper {

    public CreateClockIn toCreateClockIn(ImportedClockIn importedClockIn) {
        return
            new CreateClockIn(
                importedClockIn.businessId(),
                importedClockIn.employeeId(),
                importedClockIn.serviceId(),
                new LinkedList<>(),
                new LinkedList<>()
            );
    }

    public CreateClockInRecord toCreateClockInRecord(ImportedClockIn importedClockIn) {
        return
            new CreateClockInRecord(
                importedClockIn.date().toInstant().toEpochMilli(),
                importedClockIn.type(),
                importedClockIn.action()
            );
    }

    public UpdateClockIn toUpdateClockIn(ClockIn clockIn, CreateClockIn createClockIn) {
        return
            new UpdateClockIn(
                clockIn.id(),
                Stream
                    .concat(
                        clockIn.records().stream().map(this::toCreateClockInRecord),
                        createClockIn.records().stream()
                    )
                    .toList(),
                List.of()
            );
    }

    public CreateClockInRecord toCreateClockInRecord(ClockInRecord clockInRecord) {
        return
            new CreateClockInRecord(
                clockInRecord.date(),
                clockInRecord.type(),
                clockInRecord.action()
            );
    }
}
