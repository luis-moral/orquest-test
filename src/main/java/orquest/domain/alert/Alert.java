package orquest.domain.alert;

import orquest.domain.clockin.ClockIn;
import orquest.domain.clockin.CreateClockIn;

public class Alert {

    public boolean checkFor(ClockIn clockIn) {
        throw new UnsupportedOperationException();
    }

    public boolean checkFor(CreateClockIn clockIn) {
        throw new UnsupportedOperationException();
    }

    public String message() {
        throw new UnsupportedOperationException();
    }
}
