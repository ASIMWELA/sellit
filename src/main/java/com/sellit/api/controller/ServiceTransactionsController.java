package com.sellit.api.controller;


import com.sellit.api.Entity.*;
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
    @PostMapping("/request/{customerUuid}/{serviceUuid}")
    @Transactional
    public ResponseEntity<ApiResponse> requestService(@PathVariable @NonNull String customerUuid, @PathVariable @NonNull String serviceUuid, @RequestBody @Valid ServiceRequest serviceRequest){
        return serviceTransactions.requestService(customerUuid, serviceUuid, serviceRequest);
    }
    @PostMapping("/offer/{serviceRequestUuid}/{serviceProviderUuid}")
    @Transactional
    public ResponseEntity<ApiResponse> serviceDeliveryOffer(@NonNull @PathVariable String serviceRequestUuid, @NonNull @PathVariable String serviceProviderUuid, @RequestBody @Valid ServiceDeliveryOffer serviceDeliveryOffer){
        return serviceTransactions.serviceDeliveryOffer(serviceRequestUuid, serviceProviderUuid, serviceDeliveryOffer);
    }
    @PostMapping("/complete-offer/{serviceDeliveryOfferUuid}")
    @Transactional
    public ResponseEntity<ApiResponse> acceptServiceOffer(@NonNull @PathVariable String serviceDeliveryOfferUuid, @RequestBody @Valid ServiceAppointment serviceAppointment){
        return serviceTransactions.acceptServiceOffer(serviceDeliveryOfferUuid, serviceAppointment);
    }


}
