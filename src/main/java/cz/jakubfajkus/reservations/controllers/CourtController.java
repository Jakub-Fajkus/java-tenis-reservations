package cz.jakubfajkus.reservations.controllers;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourtController {

    @GetMapping(value = "/courts", produces = "application/json")
    public List<CourtDTO> getAllCourts() {
        return List.of(new CourtDTO(1L, "clay"),
                new CourtDTO(2L, "hard"),
                new CourtDTO(3L, "grass"),
                new CourtDTO(4L, "carpet")
        );
    }
}

