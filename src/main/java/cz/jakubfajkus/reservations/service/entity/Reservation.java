package cz.jakubfajkus.reservations.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "from_date")
    private LocalDateTime from;

    @Column(name = "to_date")
    private LocalDateTime to;

    @ManyToOne()
    @NotNull
    private Customer customer;

    @ManyToOne()
    @NotNull
    private Court court;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Match match;

    public Reservation() {
    }

    public Reservation(LocalDateTime from, LocalDateTime to, Customer customer, Court court, Match match) {
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return getFrom().equals(that.getFrom())
                && getTo().equals(that.getTo())
                && getCustomer().equals(that.getCustomer())
                && getCourt().equals(that.getCourt())
                && getMatch() == that.getMatch();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getCustomer(), getCourt(), getMatch());
    }
}
