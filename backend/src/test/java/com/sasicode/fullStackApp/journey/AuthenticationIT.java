package com.sasicode.fullStackApp.journey;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.sasicode.fullStackApp.auth.AuthenticationRequest;
import com.sasicode.fullStackApp.auth.AuthenticationResponse;
import com.sasicode.fullStackApp.customer.CustomerDTO;
import com.sasicode.fullStackApp.customer.CustomerRegisterRequest;
import com.sasicode.fullStackApp.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationIT {

    public static final String AUTHENTICATION_PATH = "api/v1/auth/login";
    public static final String CUSTOMER_URI = "api/v1/customers";
    @Autowired
    private WebTestClient webTestClient;
    private static final Random random = new Random();

    @Autowired
    private JWTUtil jwtUtil;


    @Test
    public void canLoginCustomer() {

        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + " " + UUID.randomUUID() + "@sasicode.com";
        int age = random.nextInt(1,100);

        CustomerRegisterRequest customerRegisterRequest = CustomerRegisterRequest.builder()
                .name(name)
                .email(email)
                .age(age)
                .password("password")
                .build();

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email, "password"
        );

        webTestClient.post()
                .uri(AUTHENTICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

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

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                }).returnResult();

        String token = result.getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        AuthenticationResponse authenticationResponse = result.getResponseBody();
        CustomerDTO customerDTO = authenticationResponse.customerDTO();

        assert authenticationResponse != null;
        assertThat(jwtUtil.isTokenValid(token, customerDTO.username()));
        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));


    }
}
