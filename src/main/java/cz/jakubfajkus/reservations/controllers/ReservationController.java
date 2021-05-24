package cz.jakubfajkus.reservations.controllers;

import cz.jakubfajkus.reservations.APIUris;
import cz.jakubfajkus.reservations.ReservationsStorage;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReservationController {

    private final ReservationsStorage storage;

    public ReservationController(ReservationsStorage storage) {
        this.storage = storage;
    }

    @GetMapping(value = APIUris.ROOT_URI_COURT_RESERVATIONS, produces = "application/json")
    public List<ReservationDTO> getReservationsForCourt(
            @ApiParam(value = "Id of a court") @PathVariable Long id,
            @ApiParam(value = "Starting date and time of the reservations", required = true, example = "2021-05-23T12:48:02") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime from,
            @ApiParam(value = "Ending date and time of the reservations", required = true, example = "2021-05-29T12:48:02") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime to
    ) {
        if (!from.isBefore(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the 'from' date must be before the 'to' date");
        }

        return storage.getReservations().stream()
                .filter(reservation -> reservation.getCourt().getId().equals(id))
                .filter(reservation -> reservation.getFrom().isEqual(from) || reservation.getFrom().isAfter(from))
                .filter(reservation -> reservation.getFrom().isEqual(from) || reservation.getTo().isBefore(to))
                .collect(Collectors.toList());
    }
}
