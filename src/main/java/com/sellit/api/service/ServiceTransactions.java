package com.sellit.api.service;

import com.sellit.api.Entity.*;
import com.sellit.api.dto.*;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.JsonResponse;
import com.sellit.api.payload.PageMetadata;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.repository.*;
import com.sellit.api.utils.UuidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class ServiceTransactions {

    final private ServiceCategoryRepository serviceCategoryRepository;
    final private ServiceRepository serviceRepository;
    final private UserRepository userRepository;
    final private ServiceRequestRepository serviceRequestRepository;
    final private ServiceProviderRepository serviceProviderRepository;
    final private ServiceDeliveryOfferRepository serviceDeliveryOfferRepository;
    final private ServiceAppointmentRepository serviceAppointmentRepository;
    final private ProviderRepository providerRepository;

    public ServiceTransactions(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, UserRepository userRepository, ServiceRequestRepository serviceRequestRepository, ServiceProviderRepository serviceProviderRepository, ServiceDeliveryOfferRepository serviceDeliveryOfferRepository, ServiceAppointmentRepository serviceAppointmentRepository, ProviderRepository providerRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceDeliveryOfferRepository = serviceDeliveryOfferRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
        this.providerRepository = providerRepository;
    }

    public ResponseEntity<ApiResponse> saveServiceCategory(ServiceCategory serviceCategoryRequest){
        log.info("Save service category request");
        if(serviceCategoryRepository.existsByServiceCategoryName(serviceCategoryRequest.getServiceCategoryName())){
            log.error("There is an entity with the same category name : "+ serviceCategoryRequest.getServiceCategoryName());
            throw new EntityAlreadyExistException("Category name already exists");
        }
        log.info("Building service category information");
        ServiceCategory serviceCategory = ServiceCategory.builder().serviceCategoryName(serviceCategoryRequest.getServiceCategoryName()).build();
        serviceCategory.setUuid(UuidGenerator.generateRandomString(12));
        serviceCategoryRepository.save(serviceCategory);
        log.info("Saved service category");
        return new ResponseEntity<>(new ApiResponse(true, "Category saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse> saveService(com.sellit.api.Entity.Service service, String serviceCategoryUuid){
        log.info("Save service request");
        if(serviceRepository.existsByServiceName(service.getServiceName())){
            log.error("There is an entity with the same service name : "+ service.getServiceName());

            throw new EntityAlreadyExistException("Service name already exists");
        }
        log.info("Building service information");
        ServiceCategory serviceCategory = serviceCategoryRepository.findByUuid(serviceCategoryUuid).orElseThrow(
                ()->new EntityNotFoundException("No category with the provided identifier")
        );
        service.setUuid(UuidGenerator.generateRandomString(12));
        service.setServiceCategory(serviceCategory);
        serviceRepository.save(service);
        log.info("Saved service information");
        return new ResponseEntity<>(new ApiResponse(true, "service saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<PagedResponse> getServices(int pageNo, int pageSize){
        log.info("Get service information request");
        Pageable pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<com.sellit.api.Entity.Service> services = serviceRepository.findAll(pageRequest);
        List<com.sellit.api.Entity.Service> totalNumberOfServices = serviceRepository.findAll();
        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setFirstPage(services.isFirst());
        pageMetadata.setLastPage(services.isLast());
        pageMetadata.setHasNext(services.hasNext());
        pageMetadata.setTotalNumberOfRecords(totalNumberOfServices.size());
        pageMetadata.setHasPrevious(services.hasPrevious());
        pageMetadata.setPageNumber(services.getNumber());
        pageMetadata.setPageSize(services.getSize());
        pageMetadata.setNumberOfRecordsOnPage(services.getNumberOfElements());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(services.hasNext()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/services?pageNo="+(services.getNumber()+1) + "&pageSize="+services.getSize());
        }
        if(services.hasPrevious()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/services?pageNo="+(services.getNumber()-1)+ "&pageSize="+services.getSize());
        }
        PagedResponse response = new PagedResponse();
        response.setData(services.getContent());
        response.setPageMetadata(pageMetadata);
        log.info("Returned services information");
        return new ResponseEntity<>(response,HttpStatus.OK );
    }
    public ResponseEntity<ApiResponse> requestService(String customerUuid, String serviceUuid, ServiceRequest serviceRequest){
        log.info("Requesting for a " +serviceUuid+ " by " + customerUuid);
        User customer = userRepository.findByUuid(customerUuid).orElseThrow(
                ()-> new EntityNotFoundException("No customer with the specified identifier")
        );

        com.sellit.api.Entity.Service service = serviceRepository.findByUuid(serviceUuid).orElseThrow(
                ()->new EntityNotFoundException("No service with the specified identifier")
        );
        serviceRequest.setUuid(UuidGenerator.generateRandomString(12));
        serviceRequest.setUser(customer);
        serviceRequest.setService(service);
        serviceRequestRepository.save(serviceRequest);
        log.info("Saved the request");
        return new ResponseEntity<>(new ApiResponse(true, "service request placed"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> serviceDeliveryOffer(String serviceRequestUuid, String serviceProviderUuid, ServiceDeliveryOffer serviceDeliveryOffer){
        log.info("Service offer on "+serviceProviderUuid +" by "+ serviceProviderUuid);
        ServiceRequest serviceRequest = serviceRequestRepository.findByUuid(serviceRequestUuid).orElseThrow(
                ()->new EntityNotFoundException("No request was made for the identifier provided")
        );

        ServiceProvider serviceProvider = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                ()->new EntityNotFoundException("No service provider found with the given identifier")
        );

        log.info("Building service delivery offer information");
        serviceDeliveryOffer.setUuid(UuidGenerator.generateRandomString(12));
        serviceDeliveryOffer.setServiceRequest(serviceRequest);
        serviceDeliveryOffer.setServiceProvider(serviceProvider);
        serviceDeliveryOffer.setOfferAccepted(false);
        serviceDeliveryOffer.setOfferSubmissionDate(new Date());
        serviceDeliveryOfferRepository.save(serviceDeliveryOffer);
        log.info("Saved the offer details");
       return new ResponseEntity<>(new ApiResponse(true, "offer placed"),HttpStatus.OK);
    }

    public ResponseEntity<ServiceAppointment> acceptServiceOffer(String serviceDeliveryOfferUuid, ServiceAppointment serviceAppointment){
        log.info("Accepting " +serviceDeliveryOfferUuid +" offer");
        ServiceDeliveryOffer serviceDeliveryOffer = serviceDeliveryOfferRepository.findByUuid(serviceDeliveryOfferUuid).orElseThrow(
                ()->new EntityNotFoundException("No service delivery offer with the given identifier"));

        serviceDeliveryOffer.setOfferAccepted(true);
        serviceAppointment.setServiceDeliveredOn(new Date());
        serviceAppointment.setServiceDeliveryOffer(serviceDeliveryOffer);
        serviceAppointment.setUuid(UuidGenerator.generateRandomString(12));
        serviceAppointment.setServiceDeliveryOffer(serviceDeliveryOffer);
        ServiceAppointment serviceAppointment1 = serviceAppointmentRepository.save(serviceAppointment);
        log.info("Accepted Offer " + serviceDeliveryOfferUuid);
        return new ResponseEntity<>(serviceAppointment1, HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getServiceProviders(String serviceUuid){
        log.info("Get providers for Service : {}", serviceUuid);
        com.sellit.api.Entity.Service service = serviceRepository.findByUuid(serviceUuid).orElseThrow(()->new EntityNotFoundException("No service with the given identifier"));
        List<ServiceProvider> sp=service.getServiceProviders();
        JsonResponse res = JsonResponse.builder().data(sp).build();
        log.info("Returned Providers for service : {}", serviceUuid);
        return  new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<PagedResponse> getServiceRequests(int pageNo, int pageSize){
        log.info("Get service requests");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ServiceRequest> requests = serviceRequestRepository.findAll(pageable);
        List<ServiceRequest> totalNum = serviceRequestRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(requests.isFirst())
                .lastPage(requests.isLast())
                .pageNumber(requests.getNumber())
                .pageSize(requests.getSize())
                .numberOfRecordsOnPage(requests.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(requests.hasNext())
                .hasPrevious(requests.hasPrevious())
                .build();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(requests.hasNext()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/services/requests?pageNo="+(requests.getNumber()+1) + "&pageSize="+requests.getSize());
        }
        if(requests.hasPrevious()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/services/requests?pageNo="+(requests.getNumber()-1)+ "&pageSize="+requests.getSize());
        }
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(requests.getContent()).build();
        log.info("Returned service requests");
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getOffersForARequest(String serviceRequestUuid){
        log.info("Get offers for service : {}", serviceRequestUuid);
        ServiceRequest request = serviceRequestRepository.findByUuid(serviceRequestUuid).orElseThrow(()->new EntityNotFoundException("No request with the given identifier"));
        List<ServiceDeliveryOffer> offers = request.getServiceDeliveryOffers();

       List<ServiceOfferDto> offerDtos = new ArrayList<>();

        offers.forEach(offer->{
            OfferPackage offerPackage =
                    OfferPackage.builder()
                    .estimatedCost(offer.getEstimatedCost())
                    .offerSubmissionDate(offer.getOfferSubmissionDate())
                    .discountInPercent(offer.getDiscountInPercent())
                    .uuid(offer.getUuid()).build();
            User user = offer.getServiceProvider().getProvider().getUser();
            UserDetailsDto userDetailsDto = null;
            Provider provider = offer.getServiceProvider().getProvider();
            if(user != null){
                userDetailsDto = UserDetailsDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .mobileNumber(user.getMobileNumber())
                        .userName(user.getUserName())
                        .build();
            }
            ServiceProvider serviceProvider =  offer.getServiceProvider();

            ProviderRating providerRating = offer.getServiceProvider().getProvider().getProviderRating();
            ProviderRatingDto providerRatingDto =
                    ProviderRatingDto.builder()
                            .avgPriceRating(providerRating.getAvgPriceRating())
                            .avgCommunicationRating(providerRating.getAvgCommunicationRating())
                            .avgProfessionalismRating(providerRating.getAvgProfessionalismRating())
                            .avgProficiencyRating(providerRating.getAvgProficiencyRating())
                            .avgPunctualityRating(providerRating.getAvgPunctualityRating())
                            .overallRating(providerRating.getOverallRating())
                    .build();

            ProviderDetails providerDetails =
                    ProviderDetails.builder()
                            .billingRatePerHour(serviceProvider.getBillingRatePerHour())
                            .experienceInMonths(serviceProvider.getExperienceInMonths())
                            .officeAddress(provider.getOfficeAddress())
                            .personalDetails(userDetailsDto)
                            .providerRating(providerRatingDto)
                            .serviceOfferingDescription(serviceProvider.getServiceOfferingDescription())
                            .build();
            ServiceOfferDto serviceOfferDto=
                    ServiceOfferDto.builder()
                            .offerPackage(offerPackage)
                            .providerDetails(providerDetails)
                    .build();
            offerDtos.add(serviceOfferDto);
        });

        log.info("Returned Offers for service : {}", serviceRequestUuid);
        return new ResponseEntity<>(new JsonResponse(offerDtos),HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getUserAppointments(String userUuid){
        User user = userRepository.findByUuid(userUuid).orElseThrow(
                ()->new EntityNotFoundException("No user with the identifier provided")
        );
        List<ServiceRequest> serviceRequest = user.getServiceRequests();
        List<UserAppointmentDto> serviceAppointments= new ArrayList<>();
        if(serviceRequest.size()>0){
           List<ServiceDeliveryOffer> serviceDeliveryOffers = serviceRequest.get(0).getServiceDeliveryOffers();
           if(serviceDeliveryOffers.size()>0){
               serviceDeliveryOffers.forEach(offer->{
                   if(offer.getServiceAppointments() != null){

                       Provider provider = offer.getServiceProvider().getProvider();
                       ServiceAppointment appointment = offer.getServiceAppointments();
                       User user1 = offer.getServiceProvider().getProvider().getUser();
                       ServiceProvider serviceProvider = offer.getServiceProvider();
                       UserDetailsDto userDetailsDto =
                               UserDetailsDto.builder()
                                    .userName(user1.getUserName())
                                    .email(user1.getEmail())
                                    .mobileNumber(user1.getMobileNumber())
                                .build();
                       OfferPackage offerPackage =
                               OfferPackage.builder()
                                       .discountInPercent(offer.getDiscountInPercent())
                                       .estimatedCost(offer.getEstimatedCost())
                                       .offerSubmissionDate(offer.getOfferSubmissionDate())
                               .build();
                       ProviderDetails providerDetails =
                               ProviderDetails.builder()
                               .personalDetails(userDetailsDto)
                               .billingRatePerHour(serviceProvider.getBillingRatePerHour())
                               .serviceOfferingDescription(serviceProvider.getServiceOfferingDescription())
                               .experienceInMonths(serviceProvider.getExperienceInMonths())
                               .officeAddress(provider.getOfficeAddress())
                               .build();
                       AppointmentDetails appointmentDetails =
                               AppointmentDetails.builder()
                               .providerDetails(providerDetails)
                               .offerPackage(offerPackage)
                               .build();

                       UserAppointmentDto userAppointmentDto =
                               UserAppointmentDto.builder()
                                       .serviceDeliveredOn(appointment.getServiceDeliveredOn())
                                       .serviceEndTime(appointment.getServiceEndTime())
                                       .serviceStartTime(appointment.getServiceStartTime())
                                       .uuid(appointment.getUuid())
                                       .appointmentDetails(appointmentDetails)
                                       .build();
                       serviceAppointments.add(userAppointmentDto);
                   }
               });
           }
        }
            return new ResponseEntity<>(JsonResponse.builder().data(serviceAppointments).build(), HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getProviderReviewLogs(String serviceProviderUuid){
        log.info("Get provider review logs requests");
        ServiceProvider serviceProvider = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                ()->new EntityNotFoundException("No provider with the identifier provided")
        );
        List<ProviderReviewLog> logs = new ArrayList<>();
        serviceProvider.getServiceDeliveryOffers().forEach(offer->{
            if(offer.getServiceAppointments() != null){
                if(offer.getServiceAppointments().getProviderReviewLogs().size()>0){
                    logs.addAll(offer.getServiceAppointments().getProviderReviewLogs());
                }
            }

        });

        return new ResponseEntity<>(JsonResponse.builder().data(logs).build(), HttpStatus.OK);
    }

    public ResponseEntity<PagedResponse> getCategories(Integer pageNo, Integer pageSize) {
        log.info("Get service requests");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ServiceCategory> categories = serviceCategoryRepository.findAll(pageable);
        List<ServiceCategory> totalNum = serviceCategoryRepository.findAll();
        PageMetadata pageMetadata = PageMetadata.builder()
                .firstPage(categories.isFirst())
                .lastPage(categories.isLast())
                .pageNumber(categories.getNumber())
                .pageSize(categories.getSize())
                .numberOfRecordsOnPage(categories.getNumberOfElements())
                .totalNumberOfRecords(totalNum.size())
                .hasNext(categories.hasNext())
                .hasPrevious(categories.hasPrevious())
                .build();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if(categories.hasNext()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/services/categories?pageNo="+(categories.getNumber()+1) + "&pageSize="+categories.getSize());
        }
        if(categories.hasPrevious()){
            pageMetadata.setNextPage(baseUrl+"/api/v1/services/categories?pageNo="+(categories.getNumber()-1)+ "&pageSize="+categories.getSize());
        }
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(categories.getContent()).build();
        log.info("Returned service categories requests");
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }
}
