package com.sasicode.fullStackApp.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.sasicode.fullStackApp.customer.*;
import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    public static final String CUSTOMER_URI = "api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;

    private static final Random random = new Random();

    @Test
    void canRegisterCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + " " +UUID.randomUUID() + "@sasicode.com";
        int age = random.nextInt(1,100);

        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .name(name)
                .email(email)
                .age(age)
                .password("password")
                .build();
        // do a post call
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // get all customers
        List<CustomerDTO> getAllCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        int id = getAllCustomers.stream()
                .filter(c -> c.email().equals(email))
                .findFirst()
                .map(CustomerDTO::id)
                .orElseThrow();


        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                age,
                List.of("ROLE_USER"),
                email
        );

        // make sure the customer is present
        assertThat(getAllCustomers)
                .contains(expectedCustomer);



        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }


    @Test
    void canDeleteTheCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + " " +UUID.randomUUID() + "@sasicode.com";
        int age = random.nextInt(1,100);

        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .name(name)
                .email(email)
                .password("password")
                .age(age).build();

        CustomerRegisterRequest customerRegisterRequestTwo = CustomerRegisterRequest.builder()
                .name(name)
                .email(email + ".ind")
                .password("password")
                .age(age).build();
        // do a post call
         webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequestTwo), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // get all customers
        List<Customer> getAllCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        int id = getAllCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();


        webTestClient.delete()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void updateCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + " " +UUID.randomUUID() + "@sasicode.com";
        int age = random.nextInt(1,100);

        CustomerRegisterRequest request = new CustomerRegisterRequest(
                name, email, "password", age
        );

        // send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer

        String newName = "Ali";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer  %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer oldCustomer = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .findFirst().get();


        assertThat(updatedCustomer.getEmail()).isEqualTo(oldCustomer.getEmail());
    }
}
