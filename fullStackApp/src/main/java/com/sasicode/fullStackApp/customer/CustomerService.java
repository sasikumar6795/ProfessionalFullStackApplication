package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.exception.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
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

        if(customerDao.existsCustomerWithEmail(customerRegisterRequest.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Customer registerCustomer = Customer.builder()
                .age(customerRegisterRequest.age())
                .email(customerRegisterRequest.email())
                .name(customerRegisterRequest.name())
                .build();

        customerDao.insertCustomer(registerCustomer);
    }

    public Customer updateCustomer(CustomerRegisterRequest customerRegisterRequest, Integer customerId) {
        if(customerRegisterRequest == null) {
            throw new IllegalStateException("no values were provided to update the customer");
        }
        Customer updateCustomer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        updateCustomer.setName(customerRegisterRequest.name());
        updateCustomer.setEmail(customerRegisterRequest.email());
        updateCustomer.setAge(customerRegisterRequest.age());

        customerDao.updateCustomer(updateCustomer);

        return updateCustomer;
    }

    public void deleteCustomer(Integer customerId) {

        Customer deleteCustomer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "customer with id [%s] not found".formatted(customerId)
                ));
        customerDao.deleteCustomerById(customerId);
    }
}
