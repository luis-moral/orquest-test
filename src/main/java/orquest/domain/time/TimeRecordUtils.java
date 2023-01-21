package orquest.domain.time;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TimeRecordUtils {

    private TimeRecordUtils() {
    }

    public static Optional<Long> clockInDay(List<? extends TimeRecord> records) {
        return
            records
                .stream()
                .findAny()
                .map(
                    date ->
                        Instant
                            .ofEpochMilli(date.date())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
                            .toEpochMilli()
                );
    }

    public static Optional<DayOfWeek> clockInDayOfTheWeek(List<? extends TimeRecord> records) {
        return
            records
                .stream()
                .findAny()
                .map(
                    date ->
                        Instant
                            .ofEpochMilli(date.date())
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .getDayOfWeek()
                );
    }

    public static long timeWorked(List<? extends TimeRecord> records) {
        long[] time = {0L};
        List<MatchedTimeRecord> matchedRecords = matchRecords(records);

        matchedRecords
            .forEach(
                matchedRecord -> {
                    if (matchedRecord.hasAction(TimeRecordAction.WORK)) {
                        time[0] += matchedRecord.dateDifference().orElse(0L);
                    }
                    else {
                        time[0] -= matchedRecord.dateDifference().orElse(0L);
                    }
                }
            );


        return time[0];
    }

    public static int firstRecordHourOfDay(List<? extends TimeRecord> records) {
        long minimumDate =
            records
                .stream()
                .mapToLong(TimeRecord::date)
                .summaryStatistics()
                .getMin();

        return
            ZonedDateTime
                .ofInstant(Instant.ofEpochMilli(minimumDate), ZoneOffset.UTC)
                .toLocalDateTime()
                .getHour();
    }

    public static List<MatchedTimeRecord> matchRecords(List<? extends TimeRecord> records) {
        return
            records
                .stream()
                .sorted(Comparator.comparingLong(TimeRecord::date))
                .collect(
                    LinkedList::new,
                    (list, timeRecord) -> {
                        list
                            .stream()
                            .filter(
                                matched ->
                                    // Is not matched
                                    matched.isUnmatched() &&
                                    // Has the same action that the current record
                                    matched.hasAction(timeRecord.action()) &&
                                    // Has elements missing
                                    matched.isMissing(timeRecord.type())
                            )
                            .findFirst()
                            .ifPresentOrElse(
                                // Fills the matched pair
                                matched -> matched.setByType(timeRecord),
                                // Adds a new matched pair to the list
                                () -> {
                                    MatchedTimeRecord matched = new MatchedTimeRecord();
                                    matched.setByType(timeRecord);

                                    list.add(matched);
                                }
                            );

                    },
                    LinkedList::addAll
                );
    }

    public static boolean hasMatchedRecords(List<? extends TimeRecord> records) {
        // To have matched records the amount an even value greater than 2
        if (records.isEmpty() || records.size() % 2 != 0) {
            return false;
        }

        return
            matchRecords(records)
                .stream()
                .allMatch(MatchedTimeRecord::isMatched);
    }
}
