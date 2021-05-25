package cz.jakubfajkus.reservations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.dto.Match;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ReservationsApplication.class)
@AutoConfigureMockMvc
public class CreateReservationAPITest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper jackson;


    //bussiness logic?
    // - reservation only for one day?
    // - reservation for minimal amount of minutes?
    //data integrity - multiple reservations for the same court
    // - without collision
    // - with collision


    // test reservation fails when interval dates are out of order
    @Test
    public void givenCourts_whenGivenInvalidDateRange_thenReturns400BadRequest() throws Exception {
        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 23, 12, 48),
                                LocalDateTime.of(2021, Month.MAY, 23, 11, 48),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("the 'from' date must be before the 'to' date")))

        ;
    }

    //test reservation fails when interval is not in one day
    @Test
    public void givenCourts_whenGivenDateRangeAcrossTwoDays_thenReturns400BadRequest() throws Exception {
        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 23, 23, 0),
                                LocalDateTime.of(2021, Month.MAY, 24, 0, 1),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Reservation across multiple days are not allowed")))

        ;
    }


    //    test reservation fails when date difference is too small (15-30 minutes?)
    @Test
    public void givenCourts_whenGivenDateRangeIsLessThan30Minutes_thenReturns400BadRequest() throws Exception {
        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 23, 12, 0),
                                LocalDateTime.of(2021, Month.MAY, 23, 12, 29),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Reservations are required to have at least 30 minutes")))

        ;
    }

    //    test reservation fails for unknown court id
    @Test
    public void givenCourts_whenGivenUnknownCourtId_thenReturns400BadRequest() throws Exception {
        long courtId = 99L;

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 23, 12, 0),
                                LocalDateTime.of(2021, Month.MAY, 23, 14, 0),
                                courtId
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Court with courtId " + courtId + " not found")))
        ;
    }

//    test reservation fails when court is already reserved for any part of the interval

//    test reservations succeeds when the court is available for the time interval
//    test reservation succeeds and returns correct price

//    test reservation is added to the court reservations (API)
//    test reservation is added to the user reservations (API)


    private String getReservationJson(LocalDateTime from, LocalDateTime to, long court) throws JsonProcessingException {
        CreateReservationDTO reservation = new CreateReservationDTO(
                from,
                to,
                new CustomerDTO("420432456789", "Pepa", "Slavik"),
                court,
                Match.SINGLES
        );

        return jackson.writeValueAsString(reservation);
    }

}
