package orquest.domain.time;

public interface TimeRecord {

    long date();

    TimeRecordType type();

    TimeRecordAction action();
}
