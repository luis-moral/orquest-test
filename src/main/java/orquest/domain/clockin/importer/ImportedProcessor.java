package orquest.domain.clockin.importer;

import orquest.domain.alert.Alert;
import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.ClockInFilter;
import orquest.domain.clockin.CreateClockIn;
import reactor.core.publisher.Flux;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ImportedProcessor {

    private final ImportedProcessorMapper mapper;

    public ImportedProcessor(ImportedProcessorMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Creates a ClockInFilter based on the different businessIds, employeeIds and the smaller and greater dates
     * of the provided list of ImportedClockIn.
     *
     * @param clockIns to consider
     * @return ClockInFilter
     */
    public ClockInFilter filter(List<ImportedClockIn> clockIns) {
        ImportedProcessorFields fields =
            clockIns
                .stream()
                .collect(
                    ImportedProcessorFields::new,
                    (filterFields, clockIn) -> {
                        filterFields.from(clockIn.date().toInstant().toEpochMilli());
                        filterFields.to(clockIn.date().toInstant().toEpochMilli());
                        filterFields.businessId(clockIn.businessId());
                        filterFields.employeeId(clockIn.employeeId());
                    },
                    ImportedProcessorFields::combine
                );

        return
            new ClockInFilter.Builder()
                .from(fields.from())
                .to(fields.to())
                .businessIds(fields.businessIds())
                .employeeIds(fields.employeeIds())
                .build();
    }

    /**
     * Transforms a list of ImportedClockIns to CreateClockIns. Elements from the same day, businessId and employeeId
     * will be considered a single CreateClockIn with multiple records.
     *
     * @param clockIns to transform
     * @return CreateClockIns
     */
    public Collection<CreateClockIn> group(List<ImportedClockIn> clockIns) {
        // Groups imported clock ins by day
        Map<Long, List<ImportedClockIn>> importedByDay =
            clockIns
                .stream()
                .collect(
                    HashMap::new,
                    (mapByDay, clockIn) -> {
                        List<ImportedClockIn> importedClockIns =
                            mapByDay
                                .computeIfAbsent(
                                    dayKey(clockIn),
                                    key -> new LinkedList<>()
                                );

                        importedClockIns.add(clockIn);
                    },
                    HashMap::putAll
                );

        // Transforms the grouped clock ins to CreateClockIns in with records, grouped by id (day + businessId + employeeId)
        Map<String, CreateClockIn> createById =
            importedByDay
                .entrySet()
                .stream()
                .collect(
                    HashMap::new,
                    (mapById, entry) ->
                        entry
                            .getValue()
                            .forEach(
                                importedClockIn -> {
                                    CreateClockIn createClockIn =
                                        mapById
                                            .computeIfAbsent(
                                                idKey(entry, importedClockIn),
                                                key -> mapper.toCreateClockIn(importedClockIn)
                                            );

                                    createClockIn
                                        .records()
                                        .add(
                                            mapper.toCreateClockInRecord(importedClockIn)
                                        );
                                }
                            ),
                    HashMap::putAll
                );

        return createById.values();
    }

    public Flux<CreateClockIn> process(
        List<ImportedClockIn> importedClockIns,
        List<ClockIn> currentClockIns,
        List<Alert> alerts
    ) {
        throw new UnsupportedOperationException();
    }

    private long dayKey(ImportedClockIn clockIn) {
        return clockIn.date().toLocalDateTime().toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private String idKey(Map.Entry<Long, List<ImportedClockIn>> entry, ImportedClockIn importedClockIn) {
        return entry.getKey() + "_" + importedClockIn.businessId() + "_" + importedClockIn.employeeId();
    }
}
