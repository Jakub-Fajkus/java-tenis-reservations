package cz.jakubfajkus.reservations.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CustomerDTO {

    @NotNull
    @Pattern(regexp = "^\\d+$")
    private final String telephoneNumber;

    @NotNull
    private final String firstName;

    @NotNull
    private final String lastName;

    public CustomerDTO(String telephoneNumber, String firstName, String lastName) {
        this.telephoneNumber = telephoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
