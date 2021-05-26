package cz.jakubfajkus.reservations.service.repository;

import cz.jakubfajkus.reservations.service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
