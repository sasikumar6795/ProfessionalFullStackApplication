package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.exception.DuplicateResourceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerService underTest;

    private AutoCloseable autoCloseable;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(customerDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllCustomers() {
        //Given

        //when
        underTest.getAllCustomers();

        //then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        //Given
        int id = 1;

        Customer customer = Customer.builder()
                .id(1)
                .age(17)
                .email("sasi@gmail.com")
                .name("sasi")
                .build();

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //when
        Customer actual = underTest.getCustomer(id);

        //then
        assertThat(actual).isEqualTo(customer);

    }

    @Test
    void willThrowExceptionWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id = 1;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> underTest.getCustomer(id))
        .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(id));
    }

    @Test
    void insertCustomer() {
        //Given
        String email = "sasi@gmail.com";

        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .email(email)
                .age(19)
                .name("sasi")
                .build();


        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        //when
        underTest.insertCustomer(customerRegisterRequest);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();
        assertThat(captureCustomer.getId()).isNull();
        assertThat(captureCustomer.getEmail()).isEqualTo(customerRegisterRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(customerRegisterRequest.age());
        assertThat(captureCustomer.getName()).isEqualTo(customerRegisterRequest.name());

    }


    @Test
    void whenEmailAlreadyExistsThrowException() {
        //Given
        String email = "sasi@gmail.com";

        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .email(email)
                .age(19)
                .name("sasi")
                .build();

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> underTest.insertCustomer(customerRegisterRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already exists");

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void updateCustomer() {
        //Given
        int id = 1;
        String email = "sasikumar@gmail.com";
        Customer customer = Customer.builder()
                .id(1)
                .age(17)
                .email("sasi@gmail.com")
                .name("sasi")
                .build();

        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .email(email)
                .age(19)
                .name("sasi")
                .build();
        //when
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Customer actual = underTest.updateCustomer(customerRegisterRequest, id);
        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();
        assertThat(captureCustomer.getId()).isEqualTo(id);
        assertThat(captureCustomer.getEmail()).isEqualTo(customerRegisterRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(customerRegisterRequest.age());
        assertThat(captureCustomer.getName()).isEqualTo(customerRegisterRequest.name());
        assertThat(actual).isEqualTo(captureCustomer);
    }

    @Test
    void shouldThrowExceptionWhenUpdateCustomerIsCalledWithoutCustomerRegistrationRequest() {
        //Given
        int id = 1;
        CustomerRegisterRequest customerRegisterRequest = null;
        //when
        assertThatThrownBy(() -> underTest.updateCustomer(customerRegisterRequest, id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no values were provided to update the customer");

        //then
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdateCustomerIsCalledWhenCustomerIdIsNotPresent() {
        //Given
        int customerId = -1;
        String email = "sasi@gmail.com";
        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .email(email)
                .age(19)
                .name("sasi")
                .build();
        //when
        assertThatThrownBy(() -> underTest.updateCustomer(customerRegisterRequest, customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(customerId));

        //then
        verify(customerDao,never()).updateCustomer(any());
    }

    @Test
    void deleteCustomer() {
        //Given
        int customerId = 1;

        when(customerDao.existsCustomerWithId(customerId)).thenReturn(true);

        underTest.deleteCustomer(customerId);
        //then
        verify(customerDao).deleteCustomerById(customerId);
    }

    @Test
    void shouldThrowExceptionWhendeleteCustomerCouldNotFindTheCustomerId() {
        //Given
        int customerId = -1;
        //when

        //then
        assertThatThrownBy(() -> underTest.deleteCustomer(customerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(customerId));

        //then
        verify(customerDao,never()).deleteCustomerById(any());
    }
}