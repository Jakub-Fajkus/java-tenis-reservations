package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.service.entity.Court;
import cz.jakubfajkus.reservations.service.exceptions.CourtNotFoundException;

import java.util.List;

public interface CourtService {

    List<CourtDTO> getAllCourts();

    CourtDTO find(Long id) throws CourtNotFoundException;

    CourtDTO mapEntityToDto(Court c);

}
