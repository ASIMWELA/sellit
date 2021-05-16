package com.sellit.api.service;

import com.sellit.api.Entity.Provider;
import com.sellit.api.Entity.Role;
import com.sellit.api.Entity.ServiceProvider;
import com.sellit.api.Entity.User;
import com.sellit.api.Enum.ERole;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.exception.OperationNotAllowedException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.repository.*;
import com.sellit.api.utils.UuidGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ProviderService {
    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;
    final private RoleRepository roleRepository;
    final private ProviderRepository providerRepository;
    final private ServiceProviderRepository serviceProviderRepository;
    final private ServiceRepository serviceRepository;

    public ProviderService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ProviderRepository providerRepository, ServiceProviderRepository serviceProviderRepository, ServiceRepository serviceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceRepository = serviceRepository;
    }

    public ResponseEntity<ApiResponse> signupProvider(ProviderSignupRequest providerSignupRequest){
        if(userRepository.existsByUserName(providerSignupRequest.getUserName())){
            throw new EntityAlreadyExistException("user name already taken");
        }

        if(userRepository.existsByEmail(providerSignupRequest.getEmail())){
            throw new EntityAlreadyExistException("email already taken");
        }
        if(userRepository.existsByMobileNumber(providerSignupRequest.getMobileNumber())){
            throw new EntityAlreadyExistException("mobile number already taken");
        }

        User provider = User.builder()
                .firstName(providerSignupRequest.getFirstName())
                .email(providerSignupRequest.getEmail().toLowerCase())
                .userName(providerSignupRequest.getUserName().toLowerCase())
                .isEnabled(true)
                .lastName(providerSignupRequest.getLastName())
                .mobileNumber(providerSignupRequest.getMobileNumber())
                .isAProvider(true)
                .password(providerSignupRequest.getPassword())
                .build();
        provider.setPassword(passwordEncoder.encode(provider.getPassword()));
        provider.setUuid(UuidGenerator.generateRandomString(12));
        Role roleCustomer = roleRepository.findByName(ERole.ROLE_PROVIDER).orElseThrow(
                ()->new EntityNotFoundException("Role not set")
        );
        provider.setRoles(Collections.singletonList(roleCustomer));

        Provider providerDetails = Provider.builder()
                .providerDescription(providerSignupRequest.getProviderDescription())
                .isIndividual(providerSignupRequest.isIndividual())
                .isRegisteredOffice(providerSignupRequest.isRegisteredOffice())
                .officeAddress(providerSignupRequest.getOfficeAddress())
                .build();
        if(!providerDetails.isRegisteredOffice()){
            providerDetails.setOfficeAddress(null);
        }
        providerDetails.setUuid(UuidGenerator.generateRandomString(12));
        provider.setProviderDetails(providerDetails);
        providerDetails.setUser(provider);
        userRepository.save(provider);
        return new ResponseEntity<>(new ApiResponse(true, "provider saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse> assignServiceToProvider(String serviceUuid, String providerUuid, ServiceProvider serviceProviderRequest){

        Provider provider = providerRepository.findByUuid(providerUuid).orElseThrow(
                ()-> new EntityNotFoundException("No provider with the identifier provided")
        );


        com.sellit.api.Entity.Service service = serviceRepository.findByUuid(serviceUuid).orElseThrow(
                ()->new EntityNotFoundException("No service with the provided identifier")
        );

        if(!provider.getUser().isAProvider()){
            throw new OperationNotAllowedException("You are not a provider");
        }

        provider.getServices().forEach(serv->{
            if(serv.getService().getServiceName().equalsIgnoreCase(service.getServiceName())){
                throw new EntityAlreadyExistException("Service already exist on your list");
            }
        });

        ServiceProvider serviceProvider = ServiceProvider.builder()
                .experienceInMonths(serviceProviderRequest.getExperienceInMonths())
                .serviceOfferingDescription(serviceProviderRequest.getServiceOfferingDescription())
                .billingRatePerHour(serviceProviderRequest.getBillingRatePerHour())
                .provider(provider)
                .service(service)
                .build();
        serviceProvider.setUuid(UuidGenerator.generateRandomString(12));
        serviceProviderRepository.save(serviceProvider);

        return new ResponseEntity<>(new ApiResponse(true, "service added to your list"), HttpStatus.OK);
    }

}
