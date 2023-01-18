package orquest.domain.clockin;

import java.util.List;

public class ClockInFilter {

    private Long from;
    private Long to;
    private String businessId;
    private List<String> employeeIds;

    private ClockInFilter(Builder builder) {
        from = builder.from;
        to = builder.to;
        businessId = builder.businessId;
        employeeIds = builder.employeeIds;
    }

    public Long from() {
        return from;
    }

    public Long to() {
        return to;
    }

    public String businessId() {
        return businessId;
    }

    public List<String> employeeIds() {
        return employeeIds;
    }

    public static final class Builder {
        private Long from;
        private Long to;
        private String businessId;
        private List<String> employeeIds;

        public Builder() {
        }

        public Builder from(Long val) {
            from = val;
            return this;
        }

        public Builder to(Long val) {
            to = val;
            return this;
        }

        public Builder businessId(String val) {
            businessId = val;
            return this;
        }

        public Builder employeeIds(List<String> val) {
            employeeIds = val;
            return this;
        }

        public ClockInFilter build() {
            return new ClockInFilter(this);
        }
    }
}
