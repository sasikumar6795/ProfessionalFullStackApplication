package com.sasicode.fullStackApp.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class CustomerDataJpaAccessService implements CustomerDao{

    private final CustomerRepository customerRepository;

    public CustomerDataJpaAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return Optional.empty();
    }
}
