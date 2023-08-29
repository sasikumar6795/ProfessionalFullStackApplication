package com.sasicode.fullStackApp.auth;

import lombok.Builder;

public record AuthenticationRequest(String username, String password) {
}
