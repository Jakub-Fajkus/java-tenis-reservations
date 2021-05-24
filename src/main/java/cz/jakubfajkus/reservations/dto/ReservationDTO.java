package cz.jakubfajkus.reservations.dto;

import java.time.LocalDateTime;

public class ReservationDTO {
    private LocalDateTime from;
    private LocalDateTime to;

    private CustomerDTO customer;

    private CourtDTO court;

    private Match match;

    public ReservationDTO(LocalDateTime from, LocalDateTime to, CustomerDTO customer, CourtDTO court, Match match) {
        this.from = from;
        this.to = to;
        this.customer = customer;
        this.court = court;
        this.match = match;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public CourtDTO getCourt() {
        return court;
    }

    public Match getMatch() {
        return match;
    }
}
