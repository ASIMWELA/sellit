package com.sellit.api.controller;


import com.sellit.api.Entity.Service;
import com.sellit.api.Entity.ServiceCategory;
import com.sellit.api.Entity.ServiceRequest;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.service.ServiceTransactions;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/v1/services")
@Validated
public class ServiceTransactionsController {

    private final ServiceTransactions serviceTransactions;
    public ServiceTransactionsController(ServiceTransactions serviceTransactions) {
        this.serviceTransactions = serviceTransactions;
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse> saveServiceCategory(@RequestBody @Valid ServiceCategory serviceCategory){
        return serviceTransactions.saveServiceCategory(serviceCategory);
    }
    @PostMapping("/{categoryUuid}")
    @Transactional
    public ResponseEntity<ApiResponse> saveService(@RequestBody @Valid Service service, @PathVariable("categoryUuid") String categoryUuid){
        return serviceTransactions.saveService(service, categoryUuid);
    }
    @GetMapping
    public ResponseEntity<PagedResponse> getServices(@PositiveOrZero(message = "page number cannot be negative") @RequestParam(defaultValue = "0") Integer pageNo, @Positive @RequestParam(defaultValue = "10") Integer pageSize){
        return serviceTransactions.getServices(pageNo, pageSize);
    }
    @PostMapping("/{customerUuid}/{serviceUuid}")
    @Transactional
    public ResponseEntity<ApiResponse> requestService(@PathVariable @NonNull String customerUuid, @PathVariable @NonNull String serviceUuid, @RequestBody @Valid ServiceRequest serviceRequest){
       //TODO: test request service end point
        return serviceTransactions.requestService(customerUuid, serviceUuid, serviceRequest);
    }


}
