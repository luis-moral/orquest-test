package orquest.infrastructure.handler.clockin.post;

import orquest.domain.clockin.importer.ImportedClockIn;
import orquest.infrastructure.util.validator.RequestParameterValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostClockInHandlerMapper {

    private RequestParameterValidator parameterValidator;

    public PostClockInHandlerMapper(RequestParameterValidator parameterValidator) {
        this.parameterValidator = parameterValidator;
    }

    public List<ImportedClockIn> toImportClockIns(List<PostClockInRequestItem> request) {
        return
            request
                .stream()
                .map(this::toImportedClockIn)
                .collect(Collectors.toList());
    }

    private ImportedClockIn toImportedClockIn(PostClockInRequestItem item) {
        return
            new ImportedClockIn(
                parameterValidator.mandatoryString(Optional.ofNullable(item.businessId()), "businessId"),
                parameterValidator.mandatoryDate(Optional.ofNullable(item.date()), "date"),
                parameterValidator.mandatoryString(Optional.ofNullable(item.employeeId()), "employeeId"),
                parameterValidator.mandatoryClockInRecordType(Optional.ofNullable(item.recordType()), "recordType"),
                parameterValidator.mandatoryString(Optional.ofNullable(item.serviceId()), "serviceId"),
                parameterValidator.mandatoryClockInRecordAction(Optional.ofNullable(item.type()), "type")
            );
    }
}
