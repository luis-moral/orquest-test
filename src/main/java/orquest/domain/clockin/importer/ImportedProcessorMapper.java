package orquest.domain.clockin.importer;

import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.record.CreateClockInRecord;

import java.util.LinkedList;

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
}
