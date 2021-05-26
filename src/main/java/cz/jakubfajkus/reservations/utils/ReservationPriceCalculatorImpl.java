package cz.jakubfajkus.reservations.utils;

import cz.jakubfajkus.reservations.service.entity.CourtSurface;
import cz.jakubfajkus.reservations.dto.Match;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ReservationPriceCalculatorImpl implements ReservationPriceCalculator {

    @Override
    public long calculate(ReservationDTO reservation) {
        long lengthOfReservation = Math.abs(Duration.between(reservation.getFrom(), reservation.getTo()).toMinutes());

        long price = lengthOfReservation * getPriceForCourtType(reservation.getCourt().getSurface());

        if (reservation.getMatch() == Match.DOUBLES) {
            price = (long) Math.ceil(price * 1.5);
        }

        return price;
    }

    private long getPriceForCourtType(CourtSurface surface) {
        switch (surface) {
            case CLAY:
                return 5;
            case HARD:
                return 10;
            case GRASS:
                return 15;
            case CARPET:
                return 20;
            default:
                throw new IllegalStateException("Unexpected surface type: " + surface);
        }
    }
}
