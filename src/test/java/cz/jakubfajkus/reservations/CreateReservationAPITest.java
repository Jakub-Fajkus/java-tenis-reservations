package cz.jakubfajkus.reservations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.jakubfajkus.reservations.dto.CreateReservationDTO;
import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.dto.Match;
import cz.jakubfajkus.reservations.utils.IsoDateFormatter;
import org.hamcrest.core.Every;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private ReservationsStorage storage;

    @BeforeEach
    public void beforeEach() {
        storage.reset();
    }


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

//                LocalDateTime.of(2021, Month.MAY, 24, 8, 0),
//                LocalDateTime.of(2021, Month.MAY, 24, 10, 0),

    //    test reservation fails when court is already reserved for any part of the interval
    //scenario C
    @Test
    public void givenCourts_whenCreatingAReservationForACourtThatHasAlreadyAReservationInsideTheNewReservationInterval_thenReturns400BadRequest() throws Exception {

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 24, 7, 30),
                                LocalDateTime.of(2021, Month.MAY, 24, 10, 30),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("The court is already reserved for this time period")))
        ;
    }

    //    test reservation fails when court is already reserved for any part of the interval
    //scenario D
    @Test
    public void givenCourts_whenCreatingAReservationForACourtThatHasAlreadyAReservationThatStartsInTheNewReservationInterval_thenReturns400BadRequest() throws Exception {

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 24, 7, 30),
                                LocalDateTime.of(2021, Month.MAY, 24, 8, 30),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("The court is already reserved for this time period")))
        ;
    }

    //    test reservation fails when court is already reserved for any part of the interval
    //scenario E
    @Test
    public void givenCourts_whenCreatingAReservationForACourtThatHasAlreadyAReservationThatEndsInTheNewReservationInterval_thenReturns400BadRequest() throws Exception {

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 24, 9, 30),
                                LocalDateTime.of(2021, Month.MAY, 24, 10, 30),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("The court is already reserved for this time period")))
        ;
    }

    //    test reservation fails when court is already reserved for any part of the interval
    //scenario F
    @Test
    public void givenCourts_whenCreatingAReservationForACourtThatHasAlreadyAReservationThatStartsBeforeAndEndsAfterTheNewReservationInterval_thenReturns400BadRequest() throws Exception {

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 24, 9, 0),
                                LocalDateTime.of(2021, Month.MAY, 24, 9, 30),
                                1L
                        )
                ))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("The court is already reserved for this time period")))
        ;
    }


    //    test reservation succeeds and returns correct price
    @Test
    public void givenCourts_whenCreatingAReservationForACourtThatIsAvailableForTheNewReservationInterval_thenReturns200AndCorrectPrice() throws Exception {

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 28, 10, 0),
                                LocalDateTime.of(2021, Month.MAY, 28, 12, 0),
                                1L
                        )
                ))
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(120 * 5)))

        ;
    }

    //    test reservation is added to the court reservations and user reservations (API)
    @Test
    public void givenCourts_whenCreatingAReservationForACourtThatIsAvailableForTheNewReservationInterval_thenReturns200AndReservationIsAccessibleByCourtAndCustomerAPI() throws Exception {

        mvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        getReservationJson(
                                LocalDateTime.of(2021, Month.MAY, 28, 10, 0),
                                LocalDateTime.of(2021, Month.MAY, 28, 12, 0),
                                1L
                        )
                ))
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;

        String telephoneNumber = "420432456789";

        String searchFrom = IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 28, 9, 59));
        String searchTo = IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 28, 12, 1));
        mvc.perform(get("/courts/1/reservations")
                .param("from", searchFrom)
                .param("to", searchTo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].court.id", is(1)))
                .andExpect(jsonPath("$[0].customer.telephoneNumber", is(telephoneNumber)))
        ;

        mvc.perform(get("/customers/" + telephoneNumber + "/reservations")
                .param("from", searchFrom)
                .param("to", searchTo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$..customer.telephoneNumber", Every.everyItem(is(telephoneNumber))))
                .andExpect(jsonPath("$..court.id", is(List.of(1))))
        ;
    }


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
