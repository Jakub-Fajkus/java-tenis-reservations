package cz.jakubfajkus.reservations;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.dto.CourtSurface;
import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.dto.Match;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.exceptions.CourtAlreadyReservedException;
import cz.jakubfajkus.reservations.exceptions.CourtNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReservationsStorage {

    private List<ReservationDTO> data;

    public ReservationsStorage() {
        reset();
    }

    public List<ReservationDTO> getReservations() {
        return data;
    }

    public ReservationDTO addReservation(CreateReservationDTO reservation) throws CourtNotFoundException, CourtAlreadyReservedException {
        Optional<CourtDTO> court = getCourt(reservation.getCourt());
        if (court.isEmpty()) {
            throw new CourtNotFoundException("Court with id " + reservation.getCourt() + " not found");
        }

        if (isCourtAlreadyReservedForTheTimePeriod(reservation.getFrom(), reservation.getTo())) {
            throw new CourtAlreadyReservedException();
        }

        CustomerDTO customer = createCustomerIfNeeded(reservation.getCustomer());

        ReservationDTO newReservation = new ReservationDTO(reservation.getFrom(), reservation.getTo(), customer, court.get(), reservation.getMatch());
        data.add(newReservation);

        return newReservation;
    }

    public List<ReservationDTO> getReservationsWithin(LocalDateTime from, LocalDateTime to) {
        return data.stream()
                .filter(reservation ->
                        (reservation.getFrom().isAfter(from) && reservation.getTo().isBefore(to)) ||
                                (reservation.getFrom().isAfter(from) && reservation.getFrom().isBefore(to)) ||
                                (reservation.getTo().isAfter(from) && reservation.getTo().isBefore(to)) ||
                                (reservation.getFrom().isBefore(from) && reservation.getTo().isAfter(to))
                )
                .collect(Collectors.toList());
    }

    private boolean isCourtAlreadyReservedForTheTimePeriod(LocalDateTime from, LocalDateTime to) {
        return !getReservationsWithin(from, to).isEmpty();
    }

    private CustomerDTO createCustomerIfNeeded(CustomerDTO customer) {
        return customer;
    }

    private Optional<CourtDTO> getCourt(Long courtId) {
        return data.stream().map(ReservationDTO::getCourt).filter(court -> court.getId().equals(courtId)).findAny();
    }

    public void reset() {
        data = new ArrayList<>();
        data.add(new ReservationDTO(
                LocalDateTime.of(2021, Month.MAY, 23, 13, 0),
                LocalDateTime.of(2021, Month.MAY, 23, 14, 0),
                new CustomerDTO("420123456789", "Pepa", "Novak"),
                new CourtDTO(1L, CourtSurface.CLAY),
                Match.SINGLES
        ));
        data.add(new ReservationDTO(
                LocalDateTime.of(2021, Month.MAY, 23, 14, 0),
                LocalDateTime.of(2021, Month.MAY, 23, 16, 0),
                new CustomerDTO("420432456789", "Tomas", "Sedy"),
                new CourtDTO(1L, CourtSurface.CLAY),
                Match.SINGLES
        ));
        data.add(new ReservationDTO(
                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
                LocalDateTime.of(2021, Month.MAY, 24, 10, 0),
                new CustomerDTO("420765375683", "Jiri", "Cervenka"),
                new CourtDTO(1L, CourtSurface.CLAY),
                Match.DOUBLES
        ));
        data.add(new ReservationDTO(
                LocalDateTime.of(2021, Month.MAY, 24, 12, 30),
                LocalDateTime.of(2021, Month.MAY, 24, 14, 45),
                new CustomerDTO("420908654387", "Tomas", "Maly"),
                new CourtDTO(1L, CourtSurface.CLAY),
                Match.DOUBLES
        ));
        data.add(new ReservationDTO(
                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
                LocalDateTime.of(2021, Month.MAY, 24, 10, 0),
                new CustomerDTO("420765375683", "Jiri", "Cervenka"),
                new CourtDTO(2L, CourtSurface.HARD),
                Match.DOUBLES
        ));
        data.add(new ReservationDTO(
                LocalDateTime.of(2021, Month.MAY, 24, 10, 30),
                LocalDateTime.of(2021, Month.MAY, 24, 11, 45),
                new CustomerDTO("420908654387", "Tomas", "Maly"),
                new CourtDTO(2L, CourtSurface.HARD),
                Match.DOUBLES
        ));
    }
}
