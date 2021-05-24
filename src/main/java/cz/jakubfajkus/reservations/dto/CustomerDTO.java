package cz.jakubfajkus.reservations.dto;

public class CustomerDTO {
    private final String telephoneNumber;
    private final String firstName;
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
