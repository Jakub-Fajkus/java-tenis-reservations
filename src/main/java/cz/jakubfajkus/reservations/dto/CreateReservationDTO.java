package cz.jakubfajkus.reservations.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

public class CreateReservationDTO {
    @NotNull
    private final LocalDateTime from;

    @NotNull
    private final LocalDateTime to;

    @NotNull
    private final CustomerDTO customer;

    @NotNull
    private final Long court;

    @NotNull
    private final Match match;

    public CreateReservationDTO(LocalDateTime from,
                                LocalDateTime to,
                                CustomerDTO customer,
                                Long court,
                                Match match) {
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

    public Long getCourt() {
        return court;
    }

    public Match getMatch() {
        return match;
    }

    public long getDurationInMinutes() {
        return Duration.between(from, to).toMinutes();

    }
}
