package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.service.exceptions.CourtAlreadyReservedException;
import cz.jakubfajkus.reservations.service.exceptions.CourtNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    List<ReservationDTO> getReservations();

    ReservationDTO addReservation(CreateReservationDTO reservation) throws CourtNotFoundException, CourtAlreadyReservedException;

    List<ReservationDTO> getReservationsWithin(LocalDateTime from, LocalDateTime to);
}
