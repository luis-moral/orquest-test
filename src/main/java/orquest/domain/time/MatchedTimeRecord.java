package orquest.domain.time;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Optional;

@EqualsAndHashCode
@ToString
public class MatchedTimeRecord {

    private Optional<TimeRecord> in;
    private Optional<TimeRecord> out;

    public MatchedTimeRecord() {
        this(null, null);
    }

    public MatchedTimeRecord(TimeRecord in, TimeRecord out) {
        this.in = Optional.ofNullable(in);
        this.out = Optional.ofNullable(out);
    }

    public Optional<TimeRecord> in() {
        return in;
    }

    public void in(@Nullable TimeRecord in) {
        this.in = Optional.ofNullable(in);
    }

    public Optional<TimeRecord> out() {
        return out;
    }

    public void out(@Nullable TimeRecord out) {
        this.out = Optional.ofNullable(out);
    }

    public boolean isMatched() {
        return in.isPresent() && out.isPresent();
    }

    public boolean isUnmatched() {
        return !isMatched();
    }

    public boolean hasAction(TimeRecordAction action) {
        return
            in
                .map(value -> value.action().equals(action))
                .orElseGet(() -> out.map(value -> value.action().equals(action)).orElse(false));
    }

    public boolean isMissing(TimeRecordType type) {
        return
            switch (type) {
                case IN -> in.isEmpty();
                case OUT -> out.isEmpty();
            };
    }

    public void setByType(TimeRecord timeRecord) {
        switch (timeRecord.type()) {
            case IN -> in(timeRecord);
            case OUT -> out(timeRecord);
        }
    }

    public Optional<Long> dateDifference() {
        Optional<Long> result;

        if (in.isPresent() && out.isPresent()) {
            result = Optional.of(out.get().date() - in.get().date());
        }
        else {
            result = Optional.empty();
        }

        return result;
    }
}
