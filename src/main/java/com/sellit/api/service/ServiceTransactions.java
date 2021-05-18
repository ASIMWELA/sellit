package com.sellit.api.service;

import com.sellit.api.Entity.*;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
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

    public ServiceTransactions(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, UserRepository userRepository, ServiceRequestRepository serviceRequestRepository, ServiceProviderRepository serviceProviderRepository, ServiceDeliveryOfferRepository serviceDeliveryOfferRepository, ServiceAppointmentRepository serviceAppointmentRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceDeliveryOfferRepository = serviceDeliveryOfferRepository;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
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
        pageMetadata.setPageSize((services.getSize()));
        pageMetadata.setNumberOfRecordsOnPage(services.getNumberOfElements());
        PagedResponse response = new PagedResponse();
        response.set_embedded(services.getContent());
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

    public ResponseEntity<ApiResponse> acceptServiceOffer(String serviceDeliveryOfferUuid, ServiceAppointment serviceAppointment){
        log.info("Accepting " +serviceDeliveryOfferUuid +" offer");
        ServiceDeliveryOffer serviceDeliveryOffer = serviceDeliveryOfferRepository.findByUuid(serviceDeliveryOfferUuid).orElseThrow(
                ()->new EntityNotFoundException("No service delivery offer with the given identifier"));

        serviceDeliveryOffer.setOfferAccepted(true);
        serviceAppointment.setServiceDeliveredOn(new Date());
        serviceAppointment.setServiceDeliveryOffer(serviceDeliveryOffer);
        serviceAppointment.setUuid(UuidGenerator.generateRandomString(12));
        serviceAppointmentRepository.save(serviceAppointment);
        log.info("Accepted Offer " + serviceDeliveryOfferUuid);
        return new ResponseEntity<>(new ApiResponse(true, "Appointment booked"), HttpStatus.OK);
    }

}
