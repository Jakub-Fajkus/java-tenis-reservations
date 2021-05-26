package cz.jakubfajkus.reservations.controllers;

import cz.jakubfajkus.reservations.APIUris;
import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.service.CourtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping(value = APIUris.ROOT_URI_COURTS, produces = "application/json")
    public List<CourtDTO> getAllCourts() {
        return courtService.getAllCourts();
    }
}

