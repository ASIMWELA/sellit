package com.sellit.api.controller;

import com.sellit.api.Entity.ServiceProvider;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/{serviceUuid}/{providerUuid}")
    @Transactional
    public ResponseEntity<ApiResponse> mapServiceToProvider(@PathVariable String serviceUuid, @PathVariable String providerUuid, @RequestBody @Valid ServiceProvider serviceProvider){
        return providerService.assignServiceToProvider(serviceUuid,providerUuid, serviceProvider);
    }
}
