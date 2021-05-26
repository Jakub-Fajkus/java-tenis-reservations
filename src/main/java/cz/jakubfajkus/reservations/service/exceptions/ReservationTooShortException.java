package cz.jakubfajkus.reservations.service.exceptions;

public class ReservationTooShortException extends Exception {
    private final int minimalDurationInMinutes;

    public ReservationTooShortException(int minimalDurationInMinutes) {
        this.minimalDurationInMinutes = minimalDurationInMinutes;
    }

    public int getMinimalDurationInMinutes() {
        return minimalDurationInMinutes;
    }
}
