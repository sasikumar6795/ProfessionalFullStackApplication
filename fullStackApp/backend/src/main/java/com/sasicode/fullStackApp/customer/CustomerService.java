package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.exception.DuplicateResourceException;
import com.sasicode.fullStackApp.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException(
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

    public Customer updateCustomer(CustomerUpdateRequest customerUpdateRequest, Integer customerId) {
        if(customerUpdateRequest == null) {
            throw new IllegalStateException("no values were provided to update the customer");
        }
        Customer updateCustomer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        if(customerUpdateRequest.name() != null) {
            updateCustomer.setName(customerUpdateRequest.name());
        }


        if(customerUpdateRequest.email() != null && customerDao.existsCustomerWithEmail(customerUpdateRequest.email())){
            throw new DuplicateResourceException("Email already exists");
        }else if(customerUpdateRequest.email() != null){
            updateCustomer.setEmail(customerUpdateRequest.email());
        }

        if(customerUpdateRequest.age() != null) {
            updateCustomer.setAge(customerUpdateRequest.age());
        }

        customerDao.updateCustomer(updateCustomer);

        return updateCustomer;
    }

    public void deleteCustomer(Integer customerId) {

        if(!(customerDao.existsCustomerWithId(customerId))) {
            throw new IllegalArgumentException("customer with id [%s] not found".formatted(customerId));
        }
        customerDao.deleteCustomerById(customerId);
    }
}
