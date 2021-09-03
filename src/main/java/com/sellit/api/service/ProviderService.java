package com.sellit.api.service;

import com.sellit.api.Entity.*;
import com.sellit.api.Enum.ERole;
import com.sellit.api.event.NewProviderReviewEvent;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.exception.OperationNotAllowedException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.payload.provider.UpdateProviderDetailsRequest;
import com.sellit.api.payload.provider.UpdateServiceProviderRequest;
import com.sellit.api.repository.*;
import com.sellit.api.utils.UuidGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProviderService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ProviderRepository providerRepository;
    ServiceProviderRepository serviceProviderRepository;
    ServiceRepository serviceRepository;
    ServiceAppointmentRepository serviceAppointmentRepository;
    ProviderReviewLogRepository providerReviewLogRepository;
    ApplicationEventPublisher applicationEventPublisher;

    public ProviderService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ProviderRepository providerRepository, ServiceProviderRepository serviceProviderRepository, ServiceRepository serviceRepository, ServiceAppointmentRepository serviceAppointmentRepository, ProviderReviewLogRepository providerReviewLogRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceRepository = serviceRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
        this.providerReviewLogRepository = providerReviewLogRepository;
        this.applicationEventPublisher = applicationEventPublisher;
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

        log.info("Customer signup request");
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
        log.info("Building provider details");
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

        log.info("Saving provider");
        userRepository.save(provider);
        return new ResponseEntity<>(new ApiResponse(true, "Login to add services to be complete the process"), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse> assignServiceToProvider(String serviceUuid, String providerUuid, ServiceProvider serviceProviderRequest){
        log.info("Adding a service to a provider");
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
        log.info("Building service to a provider relationship");
        ServiceProvider serviceProvider = ServiceProvider.builder()
                .experienceInMonths(serviceProviderRequest.getExperienceInMonths())
                .serviceOfferingDescription(serviceProviderRequest.getServiceOfferingDescription())
                .billingRatePerHour(serviceProviderRequest.getBillingRatePerHour())
                .provider(provider)
                .service(service)
                .build();
        serviceProvider.setUuid(UuidGenerator.generateRandomString(12));
        log.info("Saving a service-provider relationship");
        serviceProviderRepository.save(serviceProvider);

        return new ResponseEntity<>(new ApiResponse(true, "service added to your list"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> submitProviderReview(String serviceAppointmentUuid, Principal principal,ProviderReviewLog providerReviewLog){
       log.info("Request to review a provider");
        ServiceAppointment serviceAppointment = serviceAppointmentRepository.findByUuid(serviceAppointmentUuid).orElseThrow(
                ()->new EntityNotFoundException("No service appointment with the provided identifier")
        );

        String authenticatedUserName = principal.getName();

        if(!serviceAppointment.getServiceDeliveryOffer().isOfferAccepted()){
            log.error("Unaccepted review rejected");
            throw new OperationNotAllowedException("You cannot review unaccepted offer");
        }

        if(!serviceAppointment.getServiceDeliveryOffer().getServiceRequest().getUser().getUserName().equals(authenticatedUserName)){
           log.error("Unaccepted review rejected");
            throw new OperationNotAllowedException("You cannot review a provider whom you dont have a completed appointment with");
        }

        providerReviewLog.setReviewDate(new Date());
        providerReviewLog.setUuid(UuidGenerator.generateRandomString(12));
        double avgReview = (providerReviewLog.getAvgPunctualityRating()
                + providerReviewLog.getAvgProficiencyRating()
                +providerReviewLog.getAvgPriceRating()
                +providerReviewLog.getAvgProfessionalismRating()
                +providerReviewLog.getAvgCommunicationRating())/5.0;
        providerReviewLog.setOverallRating(avgReview);
        providerReviewLog.setServiceAppointment(serviceAppointment);
        providerReviewLogRepository.save(providerReviewLog);
        NewProviderReviewEvent event = new NewProviderReviewEvent(providerReviewLog.getUuid());
        applicationEventPublisher.publishEvent(event);
        return new ResponseEntity<>(new ApiResponse(true, "Review success"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> updateProvideDetails(String providerUuid, UpdateProviderDetailsRequest request){
        ServiceProvider serviceProvider = serviceProviderRepository.findByUuid(providerUuid).orElseThrow(
                ()->new EntityNotFoundException("No Provider found with the identifier provided")
        );

        Provider provider = serviceProvider.getProvider();

        if(request.isIndividual() != provider.isIndividual()){
            provider.setIndividual(request.isIndividual());
        }
        if(request.isRegisteredOffice() != provider.isRegisteredOffice()){
            provider.setRegisteredOffice(request.isRegisteredOffice());
        }
        if (request.getProviderDescription() != null) {
            provider.setProviderDescription(request.getProviderDescription());
        }
        if(request.getOfficeAddress() != null){
            provider.setOfficeAddress(request.getOfficeAddress());
        }
        providerRepository.save(provider);

        return new ResponseEntity<>(new ApiResponse(true, "Details updated"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> updateServiceProviderDetails(String serviceProviderUuid, UpdateServiceProviderRequest request){

        ServiceProvider serviceProvider = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                ()-> new EntityNotFoundException("No provider with the provided identifier")
        );

        if(request.getServiceOfferingDescription() != null){
            serviceProvider.setServiceOfferingDescription(request.getServiceOfferingDescription());
        }
        if(request.getBillingRatePerHour() != 0.0){

            serviceProvider.setBillingRatePerHour(request.getBillingRatePerHour());
        }
        if(request.getExperienceInMonths() != 0){
            serviceProvider.setExperienceInMonths(request.getExperienceInMonths());
        }
        serviceProviderRepository.save(serviceProvider);
        return new ResponseEntity<>(new ApiResponse(true, "Updated successfully"), HttpStatus.OK);
    }

}
