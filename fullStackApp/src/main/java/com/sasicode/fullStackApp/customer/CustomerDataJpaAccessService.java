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
        return customerRepository.findById(Long.valueOf(id));
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        return customerRepository.existsPersonWithId(id);
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customerRepository.deleteById(Long.valueOf(customerId));
    }

    @Override
    public void updateCustomer(Customer update) {
        customerRepository.save(update);
    }
}
