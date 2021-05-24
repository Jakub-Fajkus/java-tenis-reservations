package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.ReservationDTO;

public interface ReservationPriceCalculator {
    long calculate(ReservationDTO reservation);
}
