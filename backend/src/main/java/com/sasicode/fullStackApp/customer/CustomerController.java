package com.sasicode.fullStackApp.customer;

import com.sasicode.fullStackApp.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    /*
   @RequestMapping(
           path = "api/v1/customer",
           method = RequestMethod.GET
   )*/
    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(
            @PathVariable("customerId") Integer customerId) {
        return customerService.getCustomer(customerId);
    }


    @PostMapping
    public ResponseEntity insertCustomer(@RequestBody CustomerRegisterRequest customerRegisterRequest) {
        customerService.insertCustomer(customerRegisterRequest);
        String issuedToken = jwtUtil.issueToken(customerRegisterRequest.email(), "ROLE_USER");
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, issuedToken).build();
    }

    @PutMapping("{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId, @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        Customer updateCustomer = customerService.updateCustomer(customerUpdateRequest, customerId);
        return new ResponseEntity<>(updateCustomer,HttpStatus.OK);
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity updateCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity(HttpStatus.OK);
    }

}