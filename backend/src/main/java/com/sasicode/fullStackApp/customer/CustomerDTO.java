package com.sasicode.fullStackApp.customer;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Integer age,
        List<String> roles,
        String username
        ) {

}
