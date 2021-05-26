package cz.jakubfajkus.reservations.controllers;

import cz.jakubfajkus.reservations.APIUris;
import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.service.entity.CourtSurface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourtController {

    @GetMapping(value = APIUris.ROOT_URI_COURTS, produces = "application/json")
    public List<CourtDTO> getAllCourts() {
        return List.of(new CourtDTO(1L, CourtSurface.CLAY),
                new CourtDTO(2L, CourtSurface.HARD),
                new CourtDTO(3L, CourtSurface.GRASS),
                new CourtDTO(4L, CourtSurface.CARPET)
        );
    }
}

