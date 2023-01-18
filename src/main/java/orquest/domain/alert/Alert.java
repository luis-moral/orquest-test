package orquest.domain.alert;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.alert.ClockInAlert;

import java.util.Optional;

public class Alert {

    public Optional<ClockInAlert> checkFor(ClockIn clock) {
        throw new UnsupportedOperationException();
    }
}
