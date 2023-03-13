package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.exception.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void insertCustomer(CustomerRegisterRequest customerRegisterRequest) {

        if(customerDao.existsPersonWithEmail(customerRegisterRequest.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Customer registerCustomer = Customer.builder()
                .age(customerRegisterRequest.age())
                .email(customerRegisterRequest.email())
                .name(customerRegisterRequest.name())
                .build();

        customerDao.insertCustomer(registerCustomer);
    }
}
