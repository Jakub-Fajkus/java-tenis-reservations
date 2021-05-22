package cz.jakubfajkus.reservations.dto;

public class CourtDTO {
    private Long id;
    private String surface;

    public CourtDTO(Long id, String surface) {
        this.id = id;
        this.surface = surface;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }
}
