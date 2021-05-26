package cz.jakubfajkus.reservations.service.repository;

import cz.jakubfajkus.reservations.service.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}
