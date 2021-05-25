package com.sellit.api.service;

import com.sellit.api.Entity.*;
import com.sellit.api.Enum.ERole;
import com.sellit.api.dto.ProviderRatingDto;
import com.sellit.api.dto.ServiceProviderDto;
import com.sellit.api.dto.UserDetailsDto;
import com.sellit.api.event.NewProviderReviewEvent;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.exception.OperationNotAllowedException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PageMetadata;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.payload.provider.ProviderSignupRequest;
import com.sellit.api.repository.*;
import com.sellit.api.utils.UuidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProviderService {
    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;
    final private RoleRepository roleRepository;
    final private ProviderRepository providerRepository;
    final private ServiceProviderRepository serviceProviderRepository;
    final private ServiceRepository serviceRepository;
    final private ServiceAppointmentRepository serviceAppointmentRepository;
    final private ProviderReviewLogRepository providerReviewLogRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    public ProviderService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ProviderRepository providerRepository, ServiceProviderRepository serviceProviderRepository, ServiceRepository serviceRepository, ServiceAppointmentRepository serviceAppointmentRepository, ProviderReviewLogRepository providerReviewLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceRepository = serviceRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
        this.providerReviewLogRepository = providerReviewLogRepository;
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

        log.info("Saving customer");
        userRepository.save(provider);
        return new ResponseEntity<>(new ApiResponse(true, "provider saved"), HttpStatus.CREATED);
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

    public ResponseEntity<PagedResponse> getProviders(int pageNo, int pageSize){
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<Provider> providers = providerRepository.findAll(pageRequest);
        List<Provider> totalNum = providerRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(providers.isFirst())
                .lastPage(providers.isLast())
                .pageNumber(providers.getNumber())
                .pageSize(providers.getSize())
                .numberOfRecordsOnPage(providers.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(providers.hasNext())
                .hasPrevious(providers.hasPrevious())
                .build();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(providers.hasNext()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/providers?pageNo="+(providers.getNumber()+1) + "&pageSize="+providers.getSize());
        }
        if(providers.hasPrevious()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/providers?pageNo="+(providers.getNumber()-1)+ "&pageSize="+providers.getSize());
        }
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                                        .data(providers.getContent()).build();
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    public ResponseEntity<PagedResponse> getServiceProviders(int pageNo, int pageSize){
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<ServiceProvider> serviceProviders = serviceProviderRepository.findAll(pageRequest);
        List<ServiceProvider> totalNum = serviceProviderRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(serviceProviders.isFirst())
                .lastPage(serviceProviders.isLast())
                .pageNumber(serviceProviders.getNumber())
                .pageSize(serviceProviders.getSize())
                .numberOfRecordsOnPage(serviceProviders.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(serviceProviders.hasNext())
                .hasPrevious(serviceProviders.hasPrevious())
                .build();
        List<ServiceProviderDto> serviceProviderDtoList =  new ArrayList<>();
        serviceProviders.getContent().forEach(serviceProvider -> {
            User user = serviceProvider.getProvider().getUser();
            ProviderRating providerRating = serviceProvider.getProvider().getProviderRating();
            ProviderRatingDto providerRatingDto = new ProviderRatingDto();
            if(providerRating != null){
                providerRatingDto.setAvgPriceRating(providerRating.getAvgPriceRating());
                providerRatingDto.setAvgProficiencyRating(providerRating.getAvgProficiencyRating());
                providerRatingDto.setAvgProfessionalismRating(providerRating.getAvgProfessionalismRating());
                providerRatingDto.setAvgCommunicationRating(providerRating.getAvgCommunicationRating());
                providerRatingDto.setAvgPunctualityRating(providerRating.getAvgPunctualityRating());
                providerRatingDto.setOverallRating(providerRating.getOverallRating());
                providerRatingDto.setUpdatedOn(providerRating.getUpdatedOn());
            }
            UserDetailsDto userDetailsDto = null;
            if(user != null){
                userDetailsDto=  UserDetailsDto.builder()
                        .userName(user.getUserName())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .mobileNumber(user.getMobileNumber())
                        .lastName(user.getLastName())
                        .build();
            }
            ServiceProviderDto serviceProviderDto = ServiceProviderDto.builder()
                    .uuid(serviceProvider.getUuid())
                    .providerRating(providerRatingDto)
                    .serviceOfferingDescription(serviceProvider.getServiceOfferingDescription())
                    .billingRatePerHour(serviceProvider.getBillingRatePerHour())
                    .experienceInMonths(serviceProvider.getExperienceInMonths())
                    .userDetails(userDetailsDto)
                    .build();
            serviceProviderDtoList.add(serviceProviderDto);
        });
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(serviceProviders.hasNext()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/providers/service-providers?pageNo="+(serviceProviders.getNumber()+1) + "&pageSize="+serviceProviders.getSize());
        }
        if(serviceProviders.hasPrevious()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/providers/service-providers?pageNo="+(serviceProviders.getNumber()-1)+ "&pageSize="+serviceProviders.getSize());
        }
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(serviceProviderDtoList).build();
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

}
