package cz.jakubfajkus.reservations.utils;

import cz.jakubfajkus.reservations.dto.ReservationDTO;

import java.math.BigDecimal;

public interface ReservationPriceCalculator {
    BigDecimal calculate(ReservationDTO reservation);
}
