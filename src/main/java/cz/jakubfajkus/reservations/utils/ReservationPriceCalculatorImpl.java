package cz.jakubfajkus.reservations.utils;

import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.service.entity.CourtSurface;
import cz.jakubfajkus.reservations.service.entity.Match;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;

@Component
public class ReservationPriceCalculatorImpl implements ReservationPriceCalculator {

    @Override
    public BigDecimal calculate(ReservationDTO reservation) {
        long lengthOfReservation = Math.abs(Duration.between(reservation.getFrom(), reservation.getTo()).toMinutes());

        BigDecimal price = new BigDecimal(lengthOfReservation * getPriceForCourtType(reservation.getCourt().getSurface()));

        if (reservation.getMatch() == Match.DOUBLES) {
            price = price.multiply(new BigDecimal("1.5"));
        }

        return price.round(new MathContext(price.precision(), RoundingMode.CEILING));
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
