package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.h2Db.H2DbConfiguration;
import com.sasicode.fullStackApp.testContainers.TestContainers;
import com.sasicode.fullStackApp.testContainers.TestContainersTest;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sasicode.fullStackApp.h2Db.H2DbConfiguration.FAKER;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerJdbcTemplateAccessServiceTest extends TestContainers {
    private CustomerJdbcTemplateAccessService underTest;

    private final CustomerRowMapper customerRowMapper =  new CustomerRowMapper();

    @BeforeEach
    void setUp() {
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.addScript(new ClassPathResource("V1__Initial_Setup.sql"));
//        populator.addScript(new ClassPathResource("V2__Add_Unique_Constraint_To_Customer_Table_Column_Email.sql"));
//        populator.addScript(new ClassPathResource("V3__Add_PasswordColumn_To_Customer_Table.sql"));
//        populator.execute(getDataSource());
        underTest = new CustomerJdbcTemplateAccessService(
                getJdbcTemplate(), customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(FAKER.internet().emailAddress() + "-" + UUID.randomUUID());
        customer.setPassword("password");
        customer.setAge(19);


        underTest.insertCustomer(customer);

        //when
        List<Customer> actual = underTest.selectAllCustomers();

        //then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(email);
        customer.setPassword("password");
        customer.setAge(19);

        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();
        //when
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //then
        assertThat(actual).isPresent().hasValueSatisfying( c -> {
                    assertThat(c.getId().equals(id));
                    assertThat(c.getName().equals(customer.getName()));
                    assertThat(c.getEmail().equals(customer.getEmail()));
                    assertThat(c.getAge().equals(customer.getAge()));

                }
        );
    }


    @Test
    void existsCustomerWithEmail() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(email);
        customer.setPassword("password");
        customer.setAge(19);


        underTest.insertCustomer(customer);

        //when

        boolean actual = underTest.existsCustomerWithEmail(email);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithId() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(email);
        customer.setPassword("password");
        customer.setAge(19);


        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();

        //when

        boolean actual = underTest.existsCustomerWithId(id);

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void updateCustomer() {
        //Given

        //when

        //then
    }

    @Test
    void deleteCustomerById() {
        //Given
        String email = FAKER.internet().emailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer();
        customer.setName(FAKER.name().fullName());
        customer.setEmail(email);
        customer.setPassword("password");
        customer.setAge(19);


        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();

        //when

        underTest.deleteCustomerById(id);


        //then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

//    @AfterEach
//    public void tearDown() {
//        getJdbcTemplate().execute("DROP TABLE customer");
//    }


}