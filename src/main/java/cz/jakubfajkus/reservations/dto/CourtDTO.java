package cz.jakubfajkus.reservations.dto;

import cz.jakubfajkus.reservations.service.entity.CourtSurface;

public class CourtDTO {
    private Long id;
    private CourtSurface surface;

    public CourtDTO(Long id, CourtSurface surface) {
        this.id = id;
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
}
