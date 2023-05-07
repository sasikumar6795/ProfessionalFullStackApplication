package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.h2Db.H2DbConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.sasicode.fullStackApp.h2Db.H2DbConfiguration.FAKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


class CustomerDataJpaAccessServiceTest {


    private CustomerDataJpaAccessService underTest;

    @Mock
    private CustomerRepository customerRepository;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerDataJpaAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {

        //when
        underTest.selectAllCustomers();

        //then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        long id = 1;

        //when
        underTest.selectCustomerById((int) id);

        //then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {

        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = Customer.builder()
                .name(FAKER.name().fullName())
                .email(email)
                .age(19)
                .build();

        //when
        underTest.insertCustomer(customer);

        //then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();

        //when
        underTest.existsCustomerWithEmail(email);

        //then
        verify(customerRepository).existsCustomerByEmail(email);

    }

    @Test
    void existsCustomerWithId() {
        // Given
        int id = 1;

        // When
        underTest.existsCustomerWithId(id);

        // Then
        verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 1;

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerRepository).deleteById((long) id);
    }

    @Test
    void updateCustomer() {
        // Given
        Customer customer = new Customer(
                1, "sasi", "sasi@gmail.com", 2
        );

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository).save(customer);
    }
}