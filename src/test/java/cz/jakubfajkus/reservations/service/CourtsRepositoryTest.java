package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.service.entity.Court;
import cz.jakubfajkus.reservations.service.entity.CourtSurface;
import cz.jakubfajkus.reservations.service.repository.CourtRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest( )
@Transactional
public class CourtsRepositoryTest {

    @Autowired
    private CourtRepository courtRepository;

    @Test
    public void test() {
        Court saved = courtRepository.save(new Court(CourtSurface.CLAY));

        assertThat(saved.getId()).isNotNull();

        assertThat(courtRepository.getById(saved.getId())).isEqualTo(saved);

    }

}