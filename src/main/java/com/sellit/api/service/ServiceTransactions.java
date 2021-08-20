package com.sellit.api.service;

import com.sellit.api.Entity.*;
import com.sellit.api.Enum.ERole;
import com.sellit.api.dto.*;
import com.sellit.api.event.AppointmentEvent;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.exception.OperationNotAllowedException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.JsonResponse;
import com.sellit.api.payload.PageMetadata;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.repository.*;
import com.sellit.api.utils.UuidGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceTransactions {

    ServiceCategoryRepository serviceCategoryRepository;
    ServiceRepository serviceRepository;
    UserRepository userRepository;
    ServiceRequestRepository serviceRequestRepository;
    ServiceProviderRepository serviceProviderRepository;
    ServiceDeliveryOfferRepository serviceDeliveryOfferRepository;
    ServiceAppointmentRepository serviceAppointmentRepository;
    ProviderRepository providerRepository;
    ApplicationEventPublisher eventPublisher;

    public ServiceTransactions(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, UserRepository userRepository, ServiceRequestRepository serviceRequestRepository, ServiceProviderRepository serviceProviderRepository, ServiceDeliveryOfferRepository serviceDeliveryOfferRepository, ServiceAppointmentRepository serviceAppointmentRepository, ProviderRepository providerRepository, ApplicationEventPublisher eventPublisher) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceDeliveryOfferRepository = serviceDeliveryOfferRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
        this.providerRepository = providerRepository;
        this.eventPublisher = eventPublisher;
    }

    public ResponseEntity<ApiResponse> saveServiceCategory(ServiceCategory serviceCategoryRequest) {
        log.info("Save service category request");
        if (serviceCategoryRepository.existsByServiceCategoryName(serviceCategoryRequest.getServiceCategoryName())) {
            log.error("There is an entity with the same category name : " + serviceCategoryRequest.getServiceCategoryName());
            throw new EntityAlreadyExistException("Category name already exists");
        }
        log.info("Building service category information");
        ServiceCategory serviceCategory = ServiceCategory.builder().serviceCategoryName(serviceCategoryRequest.getServiceCategoryName()).build();
        serviceCategory.setUuid(UuidGenerator.generateRandomString(12));
        serviceCategoryRepository.save(serviceCategory);
        log.info("Saved service category");
        return new ResponseEntity<>(new ApiResponse(true, "Category saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse> saveService(com.sellit.api.Entity.Service service, String serviceCategoryUuid) {
        log.info("Save service request");
        if (serviceRepository.existsByServiceName(service.getServiceName())) {
            log.error("There is an entity with the same service name : " + service.getServiceName());

            throw new EntityAlreadyExistException("Service name already exists");
        }
        log.info("Building service information");
        ServiceCategory serviceCategory = serviceCategoryRepository.findByUuid(serviceCategoryUuid).orElseThrow(
                () -> new EntityNotFoundException("No category with the provided identifier")
        );
        service.setUuid(UuidGenerator.generateRandomString(12));
        service.setServiceCategory(serviceCategory);
        serviceRepository.save(service);
        log.info("Saved service information");
        return new ResponseEntity<>(new ApiResponse(true, "service saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<PagedResponse> getServices(int pageNo, int pageSize) {
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
        if (services.hasNext()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/services?pageNo=" + (services.getNumber() + 1) + "&pageSize=" + services.getSize());
        }
        if (services.hasPrevious()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/services?pageNo=" + (services.getNumber() - 1) + "&pageSize=" + services.getSize());
        }
        PagedResponse response = new PagedResponse();
        List<ServicesDto> servicesDtoList = new ArrayList<>();
        services.getContent().forEach(service -> {
            ServicesDto servicesDto = ServicesDto.builder()
                    .serviceName(service.getServiceName())
                    .uuid(service.getUuid())
                    .categoryName(service.getServiceCategory().getServiceCategoryName())
                    .build();
            servicesDtoList.add(servicesDto);
        });
        response.setData(servicesDtoList);
        response.setPageMetadata(pageMetadata);
        log.info("Returned services information");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> requestService(String customerUuid, String serviceUuid, ServiceRequest serviceRequest) {
        log.info("Requesting for a " + serviceUuid + " by " + customerUuid);
        User customer = userRepository.findByUuid(customerUuid).orElseThrow(
                () -> new EntityNotFoundException("No customer with the specified identifier")
        );

        com.sellit.api.Entity.Service service = serviceRepository.findByUuid(serviceUuid).orElseThrow(
                () -> new EntityNotFoundException("No service with the specified identifier")
        );
        if (!(customer.getRoles().get(0).getName().name().equals(ERole.ROLE_CUSTOMER.name()))) {
            throw new OperationNotAllowedException("You not allowed to create a request");
        }
        serviceRequest.setUuid(UuidGenerator.generateRandomString(12));
        serviceRequest.setUser(customer);
        serviceRequest.setService(service);
        serviceRequestRepository.save(serviceRequest);
        log.info("Saved the request");
        return new ResponseEntity<>(new ApiResponse(true, "service request placed"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> serviceDeliveryOffer(String serviceRequestUuid, String serviceProviderUuid, ServiceDeliveryOffer serviceDeliveryOffer) {
        log.info("Service offer on " + serviceProviderUuid + " by " + serviceProviderUuid);
        ServiceRequest serviceRequest = serviceRequestRepository.findByUuid(serviceRequestUuid).orElseThrow(
                () -> new EntityNotFoundException("No request was made for the identifier provided")
        );

        ServiceProvider serviceProvider = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                () -> new EntityNotFoundException("No service provider found with the given identifier")
        );

        log.info("Building service delivery offer information");
        serviceDeliveryOffer.setUuid(UuidGenerator.generateRandomString(12));
        serviceDeliveryOffer.setServiceRequest(serviceRequest);
        serviceDeliveryOffer.setServiceProvider(serviceProvider);
        serviceDeliveryOffer.setOfferAccepted(false);
        serviceDeliveryOffer.setOfferSubmissionDate(new Date());
        serviceDeliveryOfferRepository.save(serviceDeliveryOffer);
        log.info("Saved the offer details");
        return new ResponseEntity<>(new ApiResponse(true, "offer placed"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> acceptServiceOffer(String serviceDeliveryOfferUuid, ServiceAppointment serviceAppointment) {
        log.info("Accepting " + serviceDeliveryOfferUuid + " offer");
        ServiceDeliveryOffer serviceDeliveryOffer = serviceDeliveryOfferRepository.findByUuid(serviceDeliveryOfferUuid).orElseThrow(
                () -> new EntityNotFoundException("No service delivery offer with the given identifier"));

        serviceDeliveryOffer.setOfferAccepted(true);
        serviceAppointment.setServiceDeliveredOn(new Date());
        serviceAppointment.setServiceDeliveryOffer(serviceDeliveryOffer);
        serviceAppointment.setUuid(UuidGenerator.generateRandomString(12));
        ServiceAppointment serviceAppointment1 = serviceAppointmentRepository.save(serviceAppointment);
        serviceDeliveryOfferRepository.save(serviceDeliveryOffer);
        AppointmentEvent appointmentEvent = new AppointmentEvent(serviceAppointment1.getUuid());
        eventPublisher.publishEvent(appointmentEvent);
        log.info("Accepted Offer " + serviceDeliveryOfferUuid);
        return new ResponseEntity<>(new ApiResponse(true, "Appointment created successfully"), HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getServiceProviders(String serviceUuid) {
        log.info("Get providers for Service : {}", serviceUuid);
        com.sellit.api.Entity.Service service = serviceRepository.findByUuid(serviceUuid).orElseThrow(() -> new EntityNotFoundException("No service with the given identifier"));
        List<ServiceProvider> sp = service.getServiceProviders();
        JsonResponse res = JsonResponse.builder().data(sp).build();
        log.info("Returned Providers for service : {}", serviceUuid);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    public ResponseEntity<PagedResponse> getServiceRequests(int pageNo, int pageSize) {
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
        if (requests.hasNext()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/services/requests?pageNo=" + (requests.getNumber() + 1) + "&pageSize=" + requests.getSize());
        }
        if (requests.hasPrevious()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/services/requests?pageNo=" + (requests.getNumber() - 1) + "&pageSize=" + requests.getSize());
        }

        List<ServiceRequestDto> requestDtos = new ArrayList<>();

        requests.getContent().forEach(request -> {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(request.getRequiredOn());
            String dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : "" + calendar.get(Calendar.DAY_OF_MONTH);
            String month = calendar.get(Calendar.MONTH) < 10 ? "0" + calendar.get(Calendar.MONTH) : "" + calendar.get(Calendar.MONTH);

            Calendar getTime = Calendar.getInstance();
            getTime.setTime(request.getExpectedStartTime());

            String hour = getTime.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + getTime.get(Calendar.HOUR_OF_DAY) : "" + getTime.get(Calendar.HOUR_OF_DAY);
            String minutes = getTime.get(Calendar.MINUTE) < 10 ? "0" + getTime.get(Calendar.MINUTE) : "" + getTime.get(Calendar.MINUTE);

            ServiceRequestDto requestDto =
                    ServiceRequestDto.builder()
                            .uuid(request.getUuid())
                            .requestDescription(request.getRequirementDescription())
                            .expectedHours(request.getExpectedTentativeEffortRequiredInHours())
                            .expectedStartTime(hour + ":" + minutes)
                            .requiredDate(dayOfMonth + "-" + month + "-" + calendar.get(Calendar.YEAR))
                            .requestBy(request.getUser().getFirstName() + " " + request.getUser().getLastName())
                            .country(request.getUser().getAddress().getCountry())
                            .email(request.getUser().getEmail())
                            .locationCity(request.getUser().getAddress().getCity())
                            .build();
            requestDtos.add(requestDto);

        });
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(requestDtos).build();
        log.info("Returned service requests");
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getOffersForARequest(String serviceRequestUuid) {
        log.info("Get offers for service : {}", serviceRequestUuid);
        ServiceRequest request = serviceRequestRepository.findByUuid(serviceRequestUuid).orElseThrow(() -> new EntityNotFoundException("No request with the given identifier"));
        List<ServiceDeliveryOffer> offers = request.getServiceDeliveryOffers();

        List<OfferPackage> offerDtos = new ArrayList<>();

        offers.forEach(offer -> {
            if (!offer.isOfferAccepted()) {
                User user = offer.getServiceProvider().getProvider().getUser();
                Provider provider = offer.getServiceProvider().getProvider();
                ServiceProvider serviceProvider = offer.getServiceProvider();
                ProviderRating providerRating = offer.getServiceProvider().getProvider().getProviderRating();

                Calendar c = Calendar.getInstance();
                c.setTime(offer.getOfferSubmissionDate());

                String submissionDate = c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR);
                String overRating;

                if (providerRating != null) {
                    overRating = String.valueOf(providerRating.getOverallRating());
                } else {
                    overRating = "0.0";
                }
                OfferPackage offerPackage =
                        OfferPackage.builder()
                                .estimatedCost(offer.getEstimatedCost())
                                .offerSubmissionDate(offer.getOfferSubmissionDate())
                                .discountInPercent(offer.getDiscountInPercent())
                                .offerBy(user.getFirstName() + " " + user.getLastName())
                                .experienceInMonths(String.valueOf(serviceProvider.getExperienceInMonths()))
                                .email(user.getEmail())
                                .mobileNumber(user.getMobileNumber())
                                .location(provider.getOfficeAddress())
                                .overallRating(overRating)
                                .submissionDate(submissionDate)
                                .uuid(offer.getUuid()).build();
                offerDtos.add(offerPackage);
            }

        });

        log.info("Returned Offers for service : {}", serviceRequestUuid);
        return new ResponseEntity<>(new JsonResponse(offerDtos), HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getUserAppointments(String userUuid) {
        User user = userRepository.findByUuid(userUuid).orElseThrow(
                () -> new EntityNotFoundException("No user with the identifier provided")
        );
        List<ServiceRequest> serviceRequest = user.getServiceRequests();
        List<UserAppointmentDto> serviceAppointments = new ArrayList<>();
        if (serviceRequest.size() > 0) {
            serviceRequest.forEach(request -> {
                request.getServiceDeliveryOffers().forEach(offer -> {
                    if (offer.getServiceAppointments() != null) {
                        ServiceAppointment appointment = offer.getServiceAppointments();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String appointmentDate = formatter.format(offer.getOfferSubmissionDate());
                        User u = offer.getServiceProvider().getProvider().getUser();

                        //get start time
                        Calendar getStartTime = Calendar.getInstance();
                        getStartTime.setTime(appointment.getServiceStartTime());
                        String hour = getStartTime.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + getStartTime.get(Calendar.HOUR_OF_DAY) : "" + getStartTime.get(Calendar.HOUR_OF_DAY);
                        String minutes = getStartTime.get(Calendar.MINUTE) < 10 ? "0" + getStartTime.get(Calendar.MINUTE) : "" + getStartTime.get(Calendar.MINUTE);
                        String startTime = hour + ":" + minutes;

                        //get end time
                        Calendar getEndTime = Calendar.getInstance();
                        getStartTime.setTime(appointment.getServiceEndTime());
                        String endHour = getEndTime.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + getEndTime.get(Calendar.HOUR_OF_DAY) : "" + getEndTime.get(Calendar.HOUR_OF_DAY);
                        String endMinutes = getEndTime.get(Calendar.MINUTE) < 10 ? "0" + getEndTime.get(Calendar.MINUTE) : "" + getEndTime.get(Calendar.MINUTE);
                        String endTime = endHour + ":" + endMinutes;

                        UserAppointmentDto appointmentDto =
                                UserAppointmentDto.builder()
                                        .uuid(appointment.getUuid())
                                        .appointmentDate(appointmentDate)
                                        .appointmentDesc(appointment.getAppointmentDescription())
                                        .appointmentWith(u.getFirstName() + " " + u.getLastName())
                                        .appointmentStartTime(startTime)
                                        .providerEmail(u.getEmail())
                                        .appointmentEndTime(endTime)
                                        .providerPhone(u.getMobileNumber())
                                        .build();
                        serviceAppointments.add(appointmentDto);


                    }
                });
            });
        }
        return new ResponseEntity<>(JsonResponse.builder().data(serviceAppointments).build(), HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getProviderReviewLogs(String serviceProviderUuid) {
        log.info("Get provider review logs requests");
        ServiceProvider serviceProvider = serviceProviderRepository.findByUuid(serviceProviderUuid).orElseThrow(
                () -> new EntityNotFoundException("No provider with the identifier provided")
        );
        List<ProviderReviewLog> logs = new ArrayList<>();
        serviceProvider.getServiceDeliveryOffers().forEach(offer -> {
            if (offer.getServiceAppointments() != null) {
                if (offer.getServiceAppointments().getProviderReviewLogs().size() > 0) {
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
        if (categories.hasNext()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/services/categories?pageNo=" + (categories.getNumber() + 1) + "&pageSize=" + categories.getSize());
        }
        if (categories.hasPrevious()) {
            pageMetadata.setNextPage(baseUrl + "/api/v1/services/categories?pageNo=" + (categories.getNumber() - 1) + "&pageSize=" + categories.getSize());
        }
        PagedResponse pagedResponse = PagedResponse.builder().pageMetadata(pageMetadata)
                .data(categories.getContent()).build();
        log.info("Returned service categories requests");
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getServiceRequests(String customerUuid) {
        log.info("Get service requests for {}", customerUuid);
        User customer = userRepository.findByUuid(customerUuid).orElseThrow(
                () -> new EntityNotFoundException("No customer with the provided identifier")
        );
        List<ServiceRequest> requests = customer.getServiceRequests();

        List<CustomerRequestDto> customerRequestDtos = new ArrayList<>();
        requests.forEach(request -> {
            Calendar c = Calendar.getInstance();
            c.setTime(request.getRequiredOn());
            String dayOfMonth = c.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : "" + c.get(Calendar.DAY_OF_MONTH);
            String month = c.get(Calendar.MONTH) < 10 ? "0" + c.get(Calendar.MONTH) : "" + c.get(Calendar.MONTH);

            Calendar getTime = Calendar.getInstance();
            getTime.setTime(request.getExpectedStartTime());
            String hour = getTime.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + getTime.get(Calendar.HOUR_OF_DAY) : "" + getTime.get(Calendar.HOUR_OF_DAY);
            String minutes = getTime.get(Calendar.MINUTE) < 10 ? "0" + getTime.get(Calendar.MINUTE) : "" + getTime.get(Calendar.MINUTE);

            CustomerRequestDto requestDto = CustomerRequestDto.builder()
                    .uuid(request.getUuid())
                    .expectedHours(String.valueOf(request.getExpectedTentativeEffortRequiredInHours()))
                    .expectedStartTime(hour + ":" + minutes)
                    .requirementDescription(request.getRequirementDescription())
                    .requiredOn(dayOfMonth + "-" + month + "-" + c.get(Calendar.YEAR))
                    .build();
            customerRequestDtos.add(requestDto);
        });
        log.info("Returned service requests for customer {}", customer.getUuid());
        return new ResponseEntity<>(JsonResponse.builder().data(customerRequestDtos).build(), HttpStatus.OK);
    }

    public ResponseEntity<JsonResponse> getProviderServices(String providerUuid) {

        Provider provider = providerRepository.findByUuid(providerUuid).orElseThrow(
                () -> new EntityNotFoundException("No provider with the provider identifier")
        );
        List<ProviderServicesDto> providerServices = new ArrayList<>();
        if (provider.getServices().size() > 0) {
            provider.getServices().forEach(serviceProvider -> {
                com.sellit.api.Entity.Service s = serviceProvider.getService();
                ServiceCategory category = s.getServiceCategory();
                ProviderServicesDto providerServicesDto = ProviderServicesDto.builder()
                        .serviceName(s.getServiceName())
                        .serviceCategoryName(category.getServiceCategoryName())
                        .serviceCategoryUuid(category.getUuid())
                        .serviceUuid(s.getUuid())
                        .billingRatePerHour(serviceProvider.getBillingRatePerHour())
                        .experienceInMonths(serviceProvider.getExperienceInMonths())
                        .build();
                providerServices.add(providerServicesDto);
            });
        }
        JsonResponse jsonResponse = JsonResponse.builder().data(providerServices).build();
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }
    
    public ResponseEntity<JsonResponse> getProviderAppointments(String providerUuid){
        User user = userRepository.findByUuid(providerUuid).orElseThrow(
                ()->new EntityNotFoundException("No user with the provided identifier")
        );
        if(!user.isAProvider()){
            throw new OperationNotAllowedException("You are not a provider");
        }
        List<ProviderAppointmentDto> providerAppointments = new ArrayList<>();
        Provider providerDetails = user.getProviderDetails();
        providerDetails.getServices().forEach(service->{
            service.getServiceDeliveryOffers().forEach(offer->{
                if(offer.getServiceAppointments() != null){
                    ServiceAppointment appointment = offer.getServiceAppointments();
                    User customer = appointment.getServiceDeliveryOffer().getServiceRequest().getUser();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                    ProviderAppointmentDto providerAppointmentDto =
                            ProviderAppointmentDto.builder()
                            .appointmentDate(dateFormatter.format(appointment.getServiceDeliveredOn()))
                            .appointmentDesc(appointment.getAppointmentDescription())
                            .appointmentEndTime(timeFormatter.format(appointment.getServiceEndTime()))
                            .appointmentStartTime(timeFormatter.format(appointment.getServiceStartTime()))
                            .appointmentWith(user.getFirstName() +" " +user.getLastName())
                            .customerEmail(user.getEmail())
                            .customerPhone(customer.getMobileNumber())
                            .build();

                    providerAppointments.add(providerAppointmentDto);
                }
            });
        });
        return new ResponseEntity<>(JsonResponse.builder().data(providerAppointments).build(), HttpStatus.OK);
    }


}
