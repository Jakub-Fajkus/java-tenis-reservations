package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.service.entity.Court;
import cz.jakubfajkus.reservations.service.exceptions.CourtNotFoundException;
import cz.jakubfajkus.reservations.service.repository.CourtRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class CourtServiceImpl implements CourtService {
    private final CourtRepository courtRepository;

    public CourtServiceImpl(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public List<CourtDTO> getAllCourts() {
        return courtRepository.findAll()
                .stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourtDTO mapEntityToDto(Court c) {
        return new CourtDTO(c.getId(), c.getSurface());
    }

    public CourtDTO find(Long id) throws CourtNotFoundException {
        Optional<Court> court = courtRepository.findById(id);

        if (court.isEmpty()) {
            throw new CourtNotFoundException("Court with id " + id + " not found");
        }

        return new CourtDTO(court.get().getId(), court.get().getSurface());
    }
}
