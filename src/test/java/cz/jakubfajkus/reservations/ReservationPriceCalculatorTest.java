package cz.jakubfajkus.reservations;

import cz.jakubfajkus.reservations.dto.CourtDTO;
import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.dto.ReservationDTO;
import cz.jakubfajkus.reservations.service.entity.CourtSurface;
import cz.jakubfajkus.reservations.service.entity.Match;
import cz.jakubfajkus.reservations.utils.ReservationPriceCalculator;
import cz.jakubfajkus.reservations.utils.ReservationPriceCalculatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationPriceCalculatorTest {

    private ReservationPriceCalculator calculator;

    @BeforeEach
    void initUseCase() {
        calculator = new ReservationPriceCalculatorImpl();
    }

    @Test
    void calculatesPriceFor1MinuteClaySinglesReservation() {
        test(1, CourtSurface.CLAY, Match.SINGLES, new BigDecimal(5));
    }

    @Test
    void calculatesPriceFor10MinutesClaySinglesReservation() {
        test(10, CourtSurface.CLAY, Match.SINGLES, new BigDecimal(10 * 5));
    }


    @Test
    void calculatesPriceFor90MinutesClaySinglesReservation() {
        test(90, CourtSurface.CLAY, Match.SINGLES, new BigDecimal(90 * 5));
    }

    @Test
    void calculatesPriceFor90MinutesClayDoublesReservation() {
        test(90, CourtSurface.CLAY, Match.DOUBLES, new BigDecimal(90 * 5).multiply(new BigDecimal("1.5")));
    }


    @Test
    void calculatesPriceFor10MinutesHardSinglesReservation() {
        test(10, CourtSurface.HARD, Match.SINGLES, new BigDecimal(10 * 10));
    }

    @Test
    void calculatesPriceFor90MinutesHardSinglesReservation() {
        test(90, CourtSurface.HARD, Match.SINGLES, new BigDecimal(90 * 10));
    }

    @Test
    void calculatesPriceFor10MinutesGrassSinglesReservation() {
        test(10, CourtSurface.GRASS, Match.SINGLES, new BigDecimal(10 * 15));
    }

    @Test
    void calculatesPriceFor10MinutesCarpetSinglesReservation() {
        test(10, CourtSurface.CARPET, Match.SINGLES, new BigDecimal(10 * 20));
    }

    private void test(int minutes, CourtSurface surface, Match matchType, BigDecimal price) {
        LocalDateTime from = LocalDateTime.of(2021, Month.MAY, 24, 12, 0);

        assertThat(calculator.calculate(
                createReservation(from, from.plusMinutes(minutes), surface, matchType)
        )).isEqualByComparingTo(price);
    }

    private ReservationDTO createReservation(LocalDateTime from, LocalDateTime to, CourtSurface surface, Match matchType) {
        return new ReservationDTO(
                from,
                to,
                new CustomerDTO("420123456789", "Pepa", "Novak"),
                new CourtDTO(1L, surface),
                matchType
        );
    }
}
