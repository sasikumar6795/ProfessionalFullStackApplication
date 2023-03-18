package com.sasicode.fullStackApp.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT t FROM Customer t  WHERE t.id = ?1")
    boolean existsPersonWithId(Integer id);
}
