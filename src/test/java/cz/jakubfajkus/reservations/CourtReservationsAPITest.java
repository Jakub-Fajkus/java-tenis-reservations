package cz.jakubfajkus.reservations;

import org.hamcrest.core.Every;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ReservationsApplication.class)
@AutoConfigureMockMvc
public class CourtReservationsAPITest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void givenReservations_whenGivenInvalidDateRange_thenReturns400BadRequest() throws Exception {

        mvc.perform(get("/courts/1/reservations")
                .param("from", formatDate(LocalDateTime.of(2024, Month.MAY, 23, 12, 48)))
                .param("to", formatDate(LocalDateTime.of(2021, Month.MAY, 28, 12, 48)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void givenReservations_whenReservationsForCourt1_thenReturnsFourReservationsForCourt1() throws Exception {

        mvc.perform(get("/courts/1/reservations")
                .param("from", formatDate(LocalDateTime.of(2021, Month.MAY, 23, 12, 48, 2)))
                .param("to", formatDate(LocalDateTime.of(2021, Month.MAY, 28, 12, 48, 2)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
        ;
    }

    @Test
    public void givenReservations_whenReservationsForCourt1_thenReturnsOnlyReservationsForCourt1() throws Exception {

        mvc.perform(get("/courts/1/reservations")
                .param("from", formatDate(LocalDateTime.of(2021, Month.MAY, 23, 12, 48, 2)))
                .param("to", formatDate(LocalDateTime.of(2021, Month.MAY, 28, 12, 48, 2)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..court.id", is(List.of(1, 1, 1, 1))))
        ;
    }

    @Test
    public void givenReservations_whenReservationsForCourt1WithDateIntervalWithoutReservations_thenReturnsEmptyList() throws Exception {

        mvc.perform(get("/courts/1/reservations")
                .param("from", formatDate(LocalDateTime.of(2030, Month.MAY, 20, 12, 20)))
                .param("to", formatDate(LocalDateTime.of(2030, Month.MAY, 22, 12, 20)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

    }


    @Test
    public void givenReservations_whenReservationsForCourt1_thenReturnsOnlyReservationsWithinGivenDateTimeInterval() throws Exception {

        checkReservations(
                LocalDateTime.of(2021, Month.MAY, 23, 12, 48, 2),
                LocalDateTime.of(2021, Month.MAY, 28, 12, 48, 2),
                1L,
                4
        );
    }

    @Test
    public void givenReservations_whenReservationsForCourt2_thenReturnsOnlyReservationsWithinGivenDateTimeInterval() throws Exception {

        checkReservations(
                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
                LocalDateTime.of(2021, Month.MAY, 28, 10, 0),
                2L,
                2
        );
    }

    @Test
    public void givenReservations_whenReservationsForCourt3_thenReturnsNoReservation() throws Exception {

        checkReservations(
                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
                LocalDateTime.of(2021, Month.MAY, 28, 10, 0),
                3L,
                0
        );
    }

    private void checkReservations(LocalDateTime dateFrom, LocalDateTime dateTo, final Long courtId, int countOfReservations) throws Exception {
        String dateFromFormatted = formatDate(dateFrom);
        String dateToFormatted = formatDate(dateTo);


        mvc.perform(get("/courts/" + courtId + "/reservations")
                .param("from", dateFromFormatted)
                .param("to", dateToFormatted)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..from", Every.everyItem(greaterThanOrEqualTo(dateFromFormatted))))
                .andExpect(jsonPath("$..to", Every.everyItem(lessThanOrEqualTo(dateToFormatted))))
                .andExpect(jsonPath("$", hasSize(countOfReservations)))
        ;
    }

    private String formatDate(LocalDateTime dateFrom) {
        return dateFrom.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
