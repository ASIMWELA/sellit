package com.sellit.api.controller;


import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.customer.CustomerSignupRequest;
import com.sellit.api.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/customers")
@Validated
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse> signupCustomer(@RequestBody @Valid CustomerSignupRequest customerSignupRequest){
        return customerService.signupCustomer(customerSignupRequest);
    }

}
