package com.sasicode.fullStackApp.auth;

import com.sasicode.fullStackApp.customer.CustomerDTO;

public record AuthenticationResponse(String token, CustomerDTO customerDTO) {
}
