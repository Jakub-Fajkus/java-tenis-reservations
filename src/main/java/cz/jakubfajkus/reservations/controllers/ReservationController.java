package cz.jakubfajkus.reservations.controllers;

import cz.jakubfajkus.reservations.APIUris;
import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.service.ReservationService;
import cz.jakubfajkus.reservations.service.exceptions.CourtAlreadyReservedException;
import cz.jakubfajkus.reservations.service.exceptions.CourtNotFoundException;
import cz.jakubfajkus.reservations.service.exceptions.ReservationSpansAcrossMultipleDaysException;
import cz.jakubfajkus.reservations.service.exceptions.ReservationTooShortException;
import cz.jakubfajkus.reservations.utils.ReservationPriceCalculator;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReservationController {

    private final ReservationPriceCalculator priceCalculator;
    private final ReservationService reservationService;

    public ReservationController(ReservationPriceCalculator priceCalculator,
                                 ReservationService reservationService) {
        this.priceCalculator = priceCalculator;
        this.reservationService = reservationService;
    }

    @GetMapping(value = APIUris.ROOT_URI_COURT_RESERVATIONS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "When the `from` date is after the `to` date"),
    })
    public List<ReservationDTO> getReservationsForCourt(
            @ApiParam(value = "Id of a court") @PathVariable Long id,
            @ApiParam(value = "Starting date and time of the reservations", required = true, example = "2021-05-23T12:48:02") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime from,
            @ApiParam(value = "Ending date and time of the reservations", required = true, example = "2021-05-29T12:48:02") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime to
    ) {
        validateDates(from, to);

        return reservationService.getReservationsWithin(from, to).stream()
                .filter(reservation -> reservation.getCourt().getId().equals(id))
                .collect(Collectors.toList());
    }

    @GetMapping(value = APIUris.ROOT_URI_CUSTOMER_RESERVATIONS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "When the `from` date is after the `to` date"),
    })
    public List<ReservationDTO> getReservationsForTelephoneNumber(
            @ApiParam(value = "Telephone number of a customer (with international dialing code, but without the plus sign)") @PathVariable String telephone,
            @ApiParam(value = "Starting date and time of the reservations", required = true, example = "2021-05-23T12:48:02") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime from,
            @ApiParam(value = "Ending date and time of the reservations", required = true, example = "2021-05-29T12:48:02") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam LocalDateTime to
    ) {
        validateDates(from, to);

        return reservationService.getReservationsWithin(from, to).stream()
                .filter(reservation -> reservation.getCustomer().getTelephoneNumber().equals(telephone))
                .collect(Collectors.toList());
    }

    @PostMapping(value = APIUris.ROOT_URI_RESERVATIONS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "When reservation is shorten that minimal reservation duration\n" +
                    "When court with given `court.id` is not found\n" +
                    "When reservation `from` date is after the `to` date\n" +
                    "When court with given `court.id` is already reserved for the time period\n" +
                    "When the reservation is not for a single day"),
    })
    public long createReservation(@Validated @RequestBody CreateReservationDTO reservation) {
        validateDates(reservation.getFrom(), reservation.getTo());

        try {
            return priceCalculator.calculate(reservationService.addReservation(reservation));
        } catch (CourtNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Court with courtId " + reservation.getCourt() + " not found");
        } catch (CourtAlreadyReservedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The court is already reserved for this time period");
        } catch (ReservationSpansAcrossMultipleDaysException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation across multiple days are not allowed");
        } catch (ReservationTooShortException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservations are required to have at least " + e.getMinimalDurationInMinutes() + " minutes");
        }
    }

    private void validateDates(LocalDateTime from, LocalDateTime to) {
        if (!from.isBefore(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the 'from' date must be before the 'to' date");
        }
    }
}
