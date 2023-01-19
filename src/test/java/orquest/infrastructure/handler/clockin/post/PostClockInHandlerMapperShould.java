package orquest.infrastructure.handler.clockin.post;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import orquest.domain.clockin.importer.ImportedClockIn;
import orquest.domain.clockin.record.ClockInRecordAction;
import orquest.domain.clockin.record.ClockInRecordType;
import orquest.infrastructure.util.validator.RequestParameterValidator;
import orquest.infrastructure.util.validator.exception.ValidationException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class PostClockInHandlerMapperShould {

    private RequestParameterValidator parameterValidator;
    private PostClockInHandlerMapper mapper;

    @BeforeEach
    public void setUp() {
        parameterValidator = Mockito.mock(RequestParameterValidator.class);

        mapper = new PostClockInHandlerMapper(parameterValidator);
    }

    @Test public void
    map_request_items_to_imported_clock_ins() {
        PostClockInRequestItem itemOne = requestItem("A1", "B1", "C1", "D1", "E1", "F1");
        PostClockInRequestItem itemTwo = requestItem("A2", "B2", "C2", "D2", "E2", "F2");

        ImportedClockIn expectedOne =
            importedClockIn("businessId1", 123456789L, "employeeId1", ClockInRecordType.IN, "serviceId1", ClockInRecordAction.WORK);
        ImportedClockIn expectedTwo =
            importedClockIn("businessId2", 223456789L, "employeeId2", ClockInRecordType.OUT, "serviceId2", ClockInRecordAction.REST);

        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("A1"), "businessId"))
            .thenReturn("businessId1");
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("A2"), "businessId"))
            .thenReturn("businessId2");
        Mockito
            .when(parameterValidator.mandatoryDate(Optional.of("B1"), "date"))
            .thenReturn(Instant.ofEpochMilli(123456789L).atZone(ZoneOffset.UTC));
        Mockito
            .when(parameterValidator.mandatoryDate(Optional.of("B2"), "date"))
            .thenReturn(Instant.ofEpochMilli(223456789L).atZone(ZoneOffset.UTC));
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("C1"), "employeeId"))
            .thenReturn("employeeId1");
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("C2"), "employeeId"))
            .thenReturn("employeeId2");
        Mockito
            .when(parameterValidator.mandatoryClockInRecordType(Optional.of("D1"), "recordType"))
            .thenReturn(ClockInRecordType.IN);
        Mockito
            .when(parameterValidator.mandatoryClockInRecordType(Optional.of("D2"), "recordType"))
            .thenReturn(ClockInRecordType.OUT);
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("E1"), "serviceId"))
            .thenReturn("serviceId1");
        Mockito
            .when(parameterValidator.mandatoryString(Optional.of("E2"), "serviceId"))
            .thenReturn("serviceId2");
        Mockito
            .when(parameterValidator.mandatoryClockInRecordAction(Optional.of("F1"), "type"))
            .thenReturn(ClockInRecordAction.WORK);
        Mockito
            .when(parameterValidator.mandatoryClockInRecordAction(Optional.of("F2"), "type"))
            .thenReturn(ClockInRecordAction.REST);

        Assertions
            .assertThat(mapper.toImportClockIns(List.of(itemOne, itemTwo)))
            .containsExactlyInAnyOrder(expectedOne, expectedTwo);
    }

    @Test public void
    error_if_validation_fails() {
        PostClockInRequestItem item = Mockito.mock(PostClockInRequestItem.class);

        Mockito
            .when(parameterValidator.mandatoryString(Mockito.any(), Mockito.anyString()))
            .thenThrow(new ValidationException("Some validation error"));

        Assertions
            .assertThatThrownBy(() -> mapper.toImportClockIns(List.of(item)))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Some validation error");
    }

    private PostClockInRequestItem requestItem(
        String businessId,
        String date,
        String employeeId,
        String recordType,
        String serviceId,
        String type
    ) {
        return
            new PostClockInRequestItem(
                businessId,
                date,
                employeeId,
                recordType,
                serviceId,
                type
            );
    }

    private ImportedClockIn importedClockIn(
        String businessId,
        long date,
        String employeeId,
        ClockInRecordType action,
        String serviceId,
        ClockInRecordAction type
    ) {
        return
            new ImportedClockIn(
                businessId,
                Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC),
                employeeId,
                action,
                serviceId,
                type
            );
    }
}