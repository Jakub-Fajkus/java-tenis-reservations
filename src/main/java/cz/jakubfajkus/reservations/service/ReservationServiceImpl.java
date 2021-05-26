package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.service.entity.Reservation;
import cz.jakubfajkus.reservations.service.exceptions.CourtAlreadyReservedException;
import cz.jakubfajkus.reservations.service.exceptions.CourtNotFoundException;
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
    public ReservationDTO addReservation(CreateReservationDTO reservation) throws CourtNotFoundException, CourtAlreadyReservedException {
        CourtDTO court = courtService.find(reservation.getCourt());

        if (isCourtAlreadyReservedForTheTimePeriod(reservation.getFrom(), reservation.getTo())) {
            throw new CourtAlreadyReservedException();
        }

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

    @Override
    public List<ReservationDTO> getReservationsWithin(LocalDateTime from, LocalDateTime to) {
        //we definitely want to rewrite this into a SQL query later
        return reservationRepository.findAll().stream()
                .filter(reservation -> filterByDate(reservation, from, to))
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    private boolean filterByDate(Reservation reservation, LocalDateTime from, LocalDateTime to) {
        return (isAfterOrEqual(reservation.getFrom(), from) && isBeforeOrEqual(reservation.getTo(), to)) ||
                (isAfterOrEqual(reservation.getFrom(), from) && isBeforeOrEqual(reservation.getFrom(), to)) ||
                (isAfterOrEqual(reservation.getTo(), from) && isBeforeOrEqual(reservation.getTo(), to)) ||
                (isBeforeOrEqual(reservation.getFrom(), from) && isAfterOrEqual(reservation.getTo(), to));
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

    private boolean isCourtAlreadyReservedForTheTimePeriod(LocalDateTime from, LocalDateTime to) {
        return !getReservationsWithin(from, to).isEmpty();
    }
}
