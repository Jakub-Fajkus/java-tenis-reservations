package cz.jakubfajkus.reservations;

import cz.jakubfajkus.reservations.service.entity.Court;
import cz.jakubfajkus.reservations.service.entity.CourtSurface;
import cz.jakubfajkus.reservations.service.entity.Customer;
import cz.jakubfajkus.reservations.service.entity.Match;
import cz.jakubfajkus.reservations.service.entity.Reservation;
import cz.jakubfajkus.reservations.service.repository.CourtRepository;
import cz.jakubfajkus.reservations.service.repository.CustomerRepository;
import cz.jakubfajkus.reservations.service.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;

@Component
class DatabaseDataInitializingBean implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDataInitializingBean.class);

    private final CourtRepository courtRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;

    public DatabaseDataInitializingBean(CourtRepository courtRepository,
                                        CustomerRepository customerRepository,
                                        ReservationRepository reservationRepository) {
        this.courtRepository = courtRepository;
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public void afterPropertiesSet() {
        logger.info("DatabaseDataInitializingBean#afterPropertiesSet()");

        if (!courtRepository.findAll().isEmpty()) {
            return;
        }

        Court court1 = courtRepository.saveAndFlush(new Court(CourtSurface.CLAY));
        Court court2 = courtRepository.saveAndFlush(new Court(CourtSurface.HARD));
        Court court3 = courtRepository.saveAndFlush(new Court(CourtSurface.GRASS));
        Court court4 = courtRepository.saveAndFlush(new Court(CourtSurface.CARPET));

        Customer customerPepaNovak = customerRepository.saveAndFlush(new Customer("420123456789", "Pepa", "Novak"));
        Customer customerTomasSedy = customerRepository.saveAndFlush(new Customer("420432456789", "Tomas", "Sedy"));
        Customer customerTomasCervenka = customerRepository.saveAndFlush(new Customer("420765375683", "Jiri", "Cervenka"));
        Customer customerTomasMaly = customerRepository.saveAndFlush(new Customer("420908654387", "Tomas", "Maly"));

        reservationRepository.saveAndFlush(new Reservation(
                LocalDateTime.of(2021, Month.MAY, 23, 13, 0),
                LocalDateTime.of(2021, Month.MAY, 23, 14, 0),
                customerPepaNovak,
                court1,
                Match.SINGLES
        ));
        reservationRepository.saveAndFlush(new Reservation(
                LocalDateTime.of(2021, Month.MAY, 23, 14, 0),
                LocalDateTime.of(2021, Month.MAY, 23, 16, 0),
                customerTomasSedy,
                court1,
                Match.SINGLES
        ));
        reservationRepository.saveAndFlush(new Reservation(
                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
                LocalDateTime.of(2021, Month.MAY, 24, 10, 0),
                customerTomasCervenka,
                court1,
                Match.DOUBLES
        ));
        reservationRepository.saveAndFlush(new Reservation(
                LocalDateTime.of(2021, Month.MAY, 24, 12, 30),
                LocalDateTime.of(2021, Month.MAY, 24, 14, 45),
                customerTomasMaly,
                court1,
                Match.DOUBLES
        ));
        reservationRepository.saveAndFlush(new Reservation(
                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
                LocalDateTime.of(2021, Month.MAY, 24, 10, 0),
                customerTomasCervenka,
                court2,
                Match.DOUBLES
        ));
        reservationRepository.saveAndFlush(new Reservation(
                LocalDateTime.of(2021, Month.MAY, 24, 10, 30),
                LocalDateTime.of(2021, Month.MAY, 24, 11, 45),
                customerTomasMaly,
                court2,
                Match.DOUBLES
        ));
    }
}