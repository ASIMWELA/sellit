package com.sellit.api.controller;

import com.sellit.api.Entity.ProviderReviewLog;
import com.sellit.api.Entity.ServiceProvider;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.service.ProviderService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/providers")
@Validated
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> signupProvider(@RequestBody @Valid ProviderSignupRequest providerSignupRequest){
        return providerService.signupProvider(providerSignupRequest);
    }
    @PostMapping("/{serviceUuid}/{providerUuid}/map-service-to-provider")
    @Transactional
    public ResponseEntity<ApiResponse> mapServiceToProvider(@PathVariable String serviceUuid, @PathVariable String providerUuid, @RequestBody @Valid ServiceProvider serviceProvider){
        return providerService.assignServiceToProvider(serviceUuid,providerUuid, serviceProvider);
    }

    @PostMapping("/{serviceAppointmentUuid}/reviews")
    @Transactional
    public ResponseEntity<ApiResponse> reviewProvider(@PathVariable @NonNull String serviceAppointmentUuid, Principal principal, @RequestBody @Valid ProviderReviewLog providerReviewLog){
        return providerService.submitProviderReview(serviceAppointmentUuid,principal, providerReviewLog);
    }

    @GetMapping

    public ResponseEntity<PagedResponse> getServiceProviders(@PositiveOrZero(message = "page number cannot be negative") @RequestParam(defaultValue = "0") Integer pageNo, @Positive @RequestParam(defaultValue = "10") Integer pageSize){
        return providerService.getServiceProviders(pageNo, pageSize);
    }
}
