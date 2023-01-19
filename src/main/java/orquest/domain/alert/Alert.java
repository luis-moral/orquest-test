package orquest.domain.alert;

import orquest.domain.clockin.CreateClockIn;
import orquest.domain.clockin.UpdateClockIn;

public class Alert {

    public long id() {
        throw new UnsupportedOperationException();
    }

    public boolean checkFor(CreateClockIn clockIn) {
        throw new UnsupportedOperationException();
    }

    public boolean checkFor(UpdateClockIn clockIn) {
        throw new UnsupportedOperationException();
    }

    public String message() {
        throw new UnsupportedOperationException();
    }
}
