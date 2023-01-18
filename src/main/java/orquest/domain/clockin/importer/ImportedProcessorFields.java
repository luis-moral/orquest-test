package orquest.domain.clockin.importer;

import java.util.HashSet;
import java.util.Set;

public class ImportedProcessorFields {

    private long from;
    private long to;
    private final Set<String> businessIds;
    private final Set<String> employeeIds;

    public ImportedProcessorFields() {
        from = Long.MAX_VALUE;
        to = Long.MIN_VALUE;
        businessIds = new HashSet<>();
        employeeIds = new HashSet<>();
    }

    public long from() {
        return from;
    }

    public void from(long from) {
        this.from = Math.min(this.from, from);
    }

    public long to() {
        return to;
    }

    public void to(long to) {
        this.to = Math.max(this.to, to);
    }

    public Set<String> businessIds() {
        return businessIds;
    }

    public void businessId(String businessId) {
        businessIds.add(businessId);
    }

    public Set<String> employeeIds() {
        return employeeIds;
    }

    public void employeeId(String employeeId) {
        employeeIds.add(employeeId);
    }

    public void combine(ImportedProcessorFields filterFields) {
        this.from(filterFields.from());
        this.to(filterFields.to());
        this.businessIds.addAll(filterFields.businessIds);
        this.employeeIds.addAll(filterFields.employeeIds);
    }
}
