package cz.jakubfajkus.reservations.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Customer {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull()
    private String telephoneNumber;

    @NotNull
    @Column(nullable = false)
    private String firstName;

    @NotNull
    @Column(nullable = false)
    private String lastName;

    public Customer() {
    }

    public Customer(String telephoneNumber, String firstName, String lastName) {
        this.telephoneNumber = telephoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;

        return getTelephoneNumber().equals(customer.getTelephoneNumber())
                && getFirstName().equals(customer.getFirstName())
                && getLastName().equals(customer.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTelephoneNumber(), getFirstName(), getLastName());
    }
}
