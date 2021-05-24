package cz.jakubfajkus.reservations;

import cz.jakubfajkus.reservations.utils.IsoDateFormatter;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ReservationsApplication.class)
@AutoConfigureMockMvc
public class CustomerReservationsAPITest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void givenReservations_whenGivenInvalidDateRange_thenReturns400BadRequest() throws Exception {

        mvc.perform(get("/customers/420123456789/reservations")
                .param("from", IsoDateFormatter.format(LocalDateTime.of(2024, Month.MAY, 23, 12, 48)))
                .param("to", IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 28, 12, 48)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void givenReservations_whenReservationsForCustomer420123456789_thenReturnsOneReservation() throws Exception {

        String telephoneNumber = "420123456789";

        mvc.perform(get("/customers/" + telephoneNumber + "/reservations")
                .param("from", IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 23, 12, 48, 2)))
                .param("to", IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 28, 12, 48, 2)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$..customer.telephoneNumber", is(List.of(telephoneNumber))))
                .andExpect(jsonPath("$..court.id", is(List.of(1))))

        ;
    }

    @Test
    public void givenReservations_whenReservationsForCustomer420765375683_thenReturnsTwoReservationsForCourt1AndCourt2() throws Exception {
        String telephoneNumber = "420765375683";

        mvc.perform(get("/customers/" + telephoneNumber + "/reservations")
                .param("from", IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 24, 8, 0)))
                .param("to", IsoDateFormatter.format(LocalDateTime.of(2021, Month.MAY, 24, 10, 0)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$..customer.telephoneNumber", Every.everyItem(is(telephoneNumber))))
                .andExpect(jsonPath("$..court.id", is(List.of(1, 2))))

        ;
    }

    @Test
    public void givenReservations_whenReservationsForCustomer420765375683WithoutReservationInGivenDateInterval_thenReturnsNoReservation() throws Exception {
        String telephoneNumber = "420765375683";

        mvc.perform(get("/customers/" + telephoneNumber + "/reservations")
                .param("from", IsoDateFormatter.format(LocalDateTime.of(2030, Month.MAY, 20, 12, 20)))
                .param("to", IsoDateFormatter.format(LocalDateTime.of(2030, Month.MAY, 22, 12, 20)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

    }
}
