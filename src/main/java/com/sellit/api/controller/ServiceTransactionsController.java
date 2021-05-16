package com.sellit.api.controller;


import com.sellit.api.Entity.Service;
import com.sellit.api.Entity.ServiceCategory;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.service.ServiceTransactions;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/services")
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

}
