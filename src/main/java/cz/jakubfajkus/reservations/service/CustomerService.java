package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.service.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getAll();

    CustomerDTO findOrCreate(CustomerDTO customer);

    CustomerDTO mapEntityToDto(Customer c);

}