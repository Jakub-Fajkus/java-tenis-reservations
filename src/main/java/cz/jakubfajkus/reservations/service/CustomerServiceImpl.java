package cz.jakubfajkus.reservations.service;

import cz.jakubfajkus.reservations.dto.CustomerDTO;
import cz.jakubfajkus.reservations.service.entity.Customer;
import cz.jakubfajkus.reservations.service.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    public CustomerDTO mapEntityToDto(Customer c) {
        return new CustomerDTO(c.getId(), c.getTelephoneNumber(), c.getFirstName(), c.getLastName());
    }

    public CustomerDTO findOrCreate(CustomerDTO customer) {
        Optional<Customer> existingCustomer = customerRepository.findByTelephoneNumber(customer.getTelephoneNumber());

        if (existingCustomer.isEmpty()) {
            return mapEntityToDto(customerRepository.save(new Customer(customer.getTelephoneNumber(), customer.getFirstName(), customer.getLastName())));
        } else {
            return mapEntityToDto(existingCustomer.get());
        }
    }
}
