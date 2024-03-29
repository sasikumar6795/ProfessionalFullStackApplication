package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.exception.DuplicateResourceException;
import com.sasicode.fullStackApp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerService underTest;

    private AutoCloseable autoCloseable;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(customerDao, passwordEncoder, customerDTOMapper);
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

        Customer customer = new Customer();
                customer.setId(1);
                customer.setAge(17);
                customer.setEmail("sasi@gmail.com");
                customer.setName("sasi");
                customer.setPassword(passwordEncoder.encode("password"));


        CustomerDTO expected = customerDTOMapper.apply(customer);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //when
        CustomerDTO actual = underTest.getCustomer(id);

        //then
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void willThrowExceptionWhenGetCustomerReturnEmptyOptional() {
        //Given
        int id = 1;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> underTest.getCustomer(id))
        .isInstanceOf(ResourceNotFoundException.class)
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
                .password("password")
                .build();


        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        //when
        String hashedPassword = "$$$$@@3@#@@32";
        when(passwordEncoder.encode("password")).thenReturn(hashedPassword);
        underTest.insertCustomer(customerRegisterRequest);

        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();
        assertThat(captureCustomer.getId()).isNull();
        assertThat(captureCustomer.getEmail()).isEqualTo(customerRegisterRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(customerRegisterRequest.age());
        assertThat(captureCustomer.getName()).isEqualTo(customerRegisterRequest.name());
        assertThat(captureCustomer.getPassword()).isEqualTo(hashedPassword);
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
        Customer customer = new Customer();
        customer.setId(1);
        customer.setAge(17);
        customer.setEmail("sasi@gmail.com");
        customer.setName("sasi");


        CustomerUpdateRequest customerUpdateRequest = CustomerUpdateRequest.builder()
                .email(email)
                .age(19)
                .name("sasi")
                .build();
        //when
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Customer actual = underTest.updateCustomer(customerUpdateRequest, id);
        //then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();
        assertThat(captureCustomer.getId()).isEqualTo(id);
        assertThat(captureCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
        assertThat(captureCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(actual).isEqualTo(captureCustomer);
    }

    @Test
    void shouldThrowExceptionWhenUpdateCustomerIsCalledWithoutCustomerRegistrationRequest() {
        //Given
        int id = 1;
        CustomerUpdateRequest customerUpdateRequest = null;
        //when
        assertThatThrownBy(() -> underTest.updateCustomer(customerUpdateRequest, id))
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
        CustomerUpdateRequest customerUpdateRequest = CustomerUpdateRequest.builder()
                .email(email)
                .age(19)
                .name("sasi")
                .build();
        //when
        assertThatThrownBy(() -> underTest.updateCustomer(customerUpdateRequest, customerId))
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