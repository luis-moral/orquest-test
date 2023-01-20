package orquest.domain.time;

import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.LongSummaryStatistics;
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

    public static long timeDifference(List<? extends TimeRecord> records) {
        LongSummaryStatistics statistics =
            records
                .stream()
                .mapToLong(TimeRecord::date)
                .summaryStatistics();

        return statistics.getMax() - statistics.getMin();
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

    public static boolean hasMatchedRecords(List<? extends TimeRecord> records) {
        // To have matched records the amount an even value greater than 2
        if (records.isEmpty() || records.size() % 2 != 0) {
            return false;
        }

        // Sorted by date and grouped in pairs
        List<Tuple2<TimeRecord, Optional<TimeRecord>>> sortedAndGroupedRecords =
            records
                .stream()
                .sorted(Comparator.comparingLong(TimeRecord::date))
                .collect(
                    LinkedList::new,
                    (list, timeRecord) -> {
                        // If there are no entries or the last pair is completed
                        if (list.isEmpty() || list.getLast().getT2().isPresent()) {
                            list.add(Tuples.of(timeRecord, Optional.empty()));
                        }
                        // If the last pair second element is missing
                        else if (list.getLast().getT2().isEmpty()) {
                            TimeRecord last = list.removeLast().getT1();
                            list.add(Tuples.of(last, Optional.of(timeRecord)));
                        }
                    },
                    LinkedList::addAll
                );

        return
            sortedAndGroupedRecords
                .stream()
                .allMatch(recordPair ->
                    recordPair.getT2().isPresent() &&
                    recordPair.getT1().type() == TimeRecordType.IN &&
                    recordPair.getT2().map(value -> value.type().equals(TimeRecordType.OUT)).orElse(false)
                );
    }
}
