package cz.jakubfajkus.reservations.service.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Court {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CourtSurface surface;

    public Court() {
    }

    public Court(CourtSurface surface) {
        this.surface = surface;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourtSurface getSurface() {
        return surface;
    }

    public void setSurface(CourtSurface surface) {
        this.surface = surface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Court)) return false;
        Court court = (Court) o;
        return getId().equals(court.getId()) && getSurface() == court.getSurface();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSurface());
    }
}
