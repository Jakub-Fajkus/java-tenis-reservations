package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.service.entity.Reservation;
import cz.jakubfajkus.reservations.service.exceptions.CourtAlreadyReservedException;
import cz.jakubfajkus.reservations.service.exceptions.CourtNotFoundException;
import cz.jakubfajkus.reservations.service.exceptions.ReservationSpansAcrossMultipleDaysException;
import cz.jakubfajkus.reservations.service.exceptions.ReservationTooShortException;
import cz.jakubfajkus.reservations.service.repository.CourtRepository;
import cz.jakubfajkus.reservations.service.repository.CustomerRepository;
import cz.jakubfajkus.reservations.service.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class ReservationServiceImpl implements ReservationService {

    public static final int MINIMAL_DURATION_IN_MINUTES = 30;

    private final CourtService courtService;
    private final CustomerService customerService;

    private final ReservationRepository reservationRepository;
    private final CourtRepository courtRepository;
    private final CustomerRepository customerRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  CourtService courtService,
                                  CustomerService customerService,
                                  CourtRepository courtRepository,
                                  CustomerRepository customerRepository) {
        this.reservationRepository = reservationRepository;
        this.courtService = courtService;
        this.customerService = customerService;
        this.courtRepository = courtRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<ReservationDTO> getReservations() {
        return reservationRepository.findAll().stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    @Override
    public ReservationDTO addReservation(CreateReservationDTO reservation) throws CourtNotFoundException, CourtAlreadyReservedException, ReservationSpansAcrossMultipleDaysException, ReservationTooShortException {
        CourtDTO court = courtService.find(reservation.getCourt());

        checkThatTheReservationIsOnlyForASingleDay(reservation);
        checkThatCourtIsNotAlreadyReservedForTheTimePeriod(reservation);
        checkThatTheDurationOfTheReservationIsLongEnough(reservation);

        CustomerDTO customer = customerService.findOrCreate(reservation.getCustomer());

        return mapEntityToDto(reservationRepository.save(
                new Reservation(reservation.getFrom(),
                        reservation.getTo(),
                        customerRepository.findById(customer.getId()).get(),
                        courtRepository.findById(court.getId()).get(),
                        reservation.getMatch()
                )
        ));
    }

    private void checkThatCourtIsNotAlreadyReservedForTheTimePeriod(CreateReservationDTO reservation) throws CourtAlreadyReservedException {
        if (isCourtAlreadyReservedForTheTimePeriod(reservation.getCourt(), reservation.getFrom(), reservation.getTo())) {
            throw new CourtAlreadyReservedException();
        }
    }

    @Override
    public List<ReservationDTO> getReservationsWithin(LocalDateTime from, LocalDateTime to) {
        //we definitely want to rewrite this into a SQL query later
        return reservationRepository.findAll().stream()
                .filter(reservation -> filterByDate(reservation, from, to))
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getReservationsForCourt(Long id, LocalDateTime from, LocalDateTime to) {
        //we definitely want to rewrite this into a SQL query later
        return getReservationsWithin(from, to).stream()
                .filter(reservation -> reservation.getCourt().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getReservationsForCustomer(String telephone, LocalDateTime from, LocalDateTime to) {
        return getReservationsWithin(from, to).stream()
                .filter(reservation -> reservation.getCustomer().getTelephoneNumber().equals(telephone))
                .collect(Collectors.toList());
    }

    private void checkThatTheReservationIsOnlyForASingleDay(CreateReservationDTO reservation) throws ReservationSpansAcrossMultipleDaysException {
        if (reservation.getFrom().getYear() != reservation.getTo().getYear()
                || reservation.getFrom().getMonth() != reservation.getTo().getMonth()
                || reservation.getFrom().getDayOfMonth() != reservation.getTo().getDayOfMonth()) {

            throw new ReservationSpansAcrossMultipleDaysException();
        }
    }

    private void checkThatTheDurationOfTheReservationIsLongEnough(CreateReservationDTO reservation) throws ReservationTooShortException {
        if (reservation.getDurationInMinutes() < MINIMAL_DURATION_IN_MINUTES) {
            throw new ReservationTooShortException(MINIMAL_DURATION_IN_MINUTES);
        }
    }

    private boolean filterByDate(Reservation reservation, LocalDateTime from, LocalDateTime to) {
        return (isAfterOrEqual(reservation.getFrom(), from) && isBeforeOrEqual(reservation.getTo(), to)) || //a reservation lies inside the interval
                (isAfterOrEqual(reservation.getFrom(), from) && isBeforeOrEqual(reservation.getFrom(), to)) || //a reservation starts inside the interval
                (isAfterOrEqual(reservation.getTo(), from) && isBeforeOrEqual(reservation.getTo(), to)) || //a reservation ends inside the interval
                (isBeforeOrEqual(reservation.getFrom(), from) && isAfterOrEqual(reservation.getTo(), to)); //a reservation starts before and ends after the interval
    }

    private boolean isAfterOrEqual(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) || a.equals(b);
    }

    private boolean isBeforeOrEqual(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b) || a.equals(b);
    }

    private ReservationDTO mapEntityToDto(Reservation r) {
        return new ReservationDTO(
                r.getId(),
                r.getFrom(),
                r.getTo(),
                customerService.mapEntityToDto(r.getCustomer()),
                courtService.mapEntityToDto(r.getCourt()),
                r.getMatch());
    }

    private boolean isCourtAlreadyReservedForTheTimePeriod(Long courtId, LocalDateTime from, LocalDateTime to) {
        return !getReservationsForCourt(courtId, from, to).isEmpty();
    }
}
