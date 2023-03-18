package com.sasicode.fullStackApp.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
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
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId, @RequestBody CustomerRegisterRequest customerRegisterRequest) {
        Customer updateCustomer = customerService.updateCustomer(customerRegisterRequest, customerId);
        return new ResponseEntity<>(updateCustomer,HttpStatus.OK);
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity updateCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return new ResponseEntity(HttpStatus.OK);
    }

}