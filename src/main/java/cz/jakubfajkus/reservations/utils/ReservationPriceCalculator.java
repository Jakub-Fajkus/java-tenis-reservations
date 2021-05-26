package cz.jakubfajkus.reservations.utils;

import cz.jakubfajkus.reservations.dto.ReservationDTO;

public interface ReservationPriceCalculator {
    long calculate(ReservationDTO reservation);
}
