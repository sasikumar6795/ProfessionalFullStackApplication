package com.sasicode.fullStackApp.customer;

import lombok.Builder;

@Builder
public record CustomerRegisterRequest(String name, String email, Integer age) {
}
