package orquest.domain.clockin;

import java.util.Set;

public class ClockInFilter {

    private Long from;
    private Long to;
    private Set<String> businessIds;
    private Set<String> employeeIds;

    private ClockInFilter(Builder builder) {
        from = builder.from;
        to = builder.to;
        businessIds = builder.businessIds;
        employeeIds = builder.employeeIds;
    }

    public Long from() {
        return from;
    }

    public Long to() {
        return to;
    }

    public Set<String> businessIds() {
        return businessIds;
    }

    public Set<String> employeeIds() {
        return employeeIds;
    }

    public static final class Builder {
        private Long from;
        private Long to;
        private Set<String> businessIds;
        private Set<String> employeeIds;

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

        public Builder businessIds(Set<String> val) {
            businessIds = val;
            return this;
        }

        public Builder employeeIds(Set<String> val) {
            employeeIds = val;
            return this;
        }

        public ClockInFilter build() {
            return new ClockInFilter(this);
        }
    }
}
