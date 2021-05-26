package cz.jakubfajkus.reservations.dto;

import cz.jakubfajkus.reservations.service.entity.Match;

import java.time.LocalDateTime;

public class ReservationDTO {
    private Long id;

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

    public ReservationDTO(Long id, LocalDateTime from, LocalDateTime to, CustomerDTO customer, CourtDTO court, Match match) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.customer = customer;
        this.court = court;
        this.match = match;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public CourtDTO getCourt() {
        return court;
    }

    public void setCourt(CourtDTO court) {
        this.court = court;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
