package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.exception.DuplicateResourceException;
import com.sasicode.fullStackApp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    private final PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers().stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void insertCustomer(CustomerRegisterRequest customerRegisterRequest) {

        if(customerDao.existsCustomerWithEmail(customerRegisterRequest.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Customer registerCustomer = new Customer();
        registerCustomer.setAge(customerRegisterRequest.age());
        registerCustomer.setEmail(customerRegisterRequest.email());
        registerCustomer.setPassword(passwordEncoder.encode(customerRegisterRequest.password()));
        registerCustomer.setName(customerRegisterRequest.name());


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
