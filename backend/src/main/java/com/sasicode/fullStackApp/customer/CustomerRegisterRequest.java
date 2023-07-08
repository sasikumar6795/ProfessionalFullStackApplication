package com.sasicode.fullStackApp.customer;

import lombok.Builder;

@Builder
public record CustomerRegisterRequest(String name, String email, String password, Integer age) {
}
