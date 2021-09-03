package com.sellit.api.controller;

import com.sellit.api.Entity.ProviderReviewLog;
import com.sellit.api.Entity.ServiceProvider;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.payload.provider.UpdateProviderDetailsRequest;
import com.sellit.api.payload.provider.UpdateServiceProviderRequest;
import com.sellit.api.service.ProviderService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

    @Secured("ROLE_PROVIDER")
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

    @Secured("ROLE_PROVIDER")
    @PutMapping("/providerUuid/update")
    public ResponseEntity<ApiResponse> updateProviderDetails(@PathVariable String providerUuid, @RequestBody @Valid UpdateProviderDetailsRequest updateProviderDetailsRequest){
        return providerService.updateProvideDetails(providerUuid, updateProviderDetailsRequest);
    }
    @Secured("ROLE_PROVIDER")
    @PutMapping("/providerUuid/update-services-details")
    public ResponseEntity<ApiResponse> updateServiceOfferingDetails(@PathVariable String providerUuid, @RequestBody @Valid UpdateServiceProviderRequest updateProviderDetailsRequest){
        return providerService.updateServiceProviderDetails(providerUuid, updateProviderDetailsRequest);
    }
}
