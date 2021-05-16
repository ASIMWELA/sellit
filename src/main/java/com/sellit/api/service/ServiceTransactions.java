package com.sellit.api.service;

import com.sellit.api.Entity.ServiceCategory;
import com.sellit.api.Entity.ServiceRequest;
import com.sellit.api.Entity.User;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PageMetadata;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.repository.*;
import com.sellit.api.utils.UuidGenerator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class ServiceTransactions {

    final private ServiceCategoryRepository serviceCategoryRepository;
    final private ServiceRepository serviceRepository;
    final private UserRepository userRepository;
    final private ServiceRequestRepository serviceRequestRepository;

    public ServiceTransactions(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, UserRepository userRepository, ServiceRequestRepository serviceRequestRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    public ResponseEntity<ApiResponse> saveServiceCategory(ServiceCategory serviceCategoryRequest){
        if(serviceCategoryRepository.existsByServiceCategoryName(serviceCategoryRequest.getServiceCategoryName())){
            throw new EntityAlreadyExistException("Category name already exists");
        }

        ServiceCategory serviceCategory = ServiceCategory.builder().serviceCategoryName(serviceCategoryRequest.getServiceCategoryName()).build();
        serviceCategory.setUuid(UuidGenerator.generateRandomString(12));
        serviceCategoryRepository.save(serviceCategory);
        return new ResponseEntity<>(new ApiResponse(true, "Category saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse> saveService(com.sellit.api.Entity.Service service, String serviceCategoryUuid){
        if(serviceRepository.existsByServiceName(service.getServiceName())){
            throw new EntityAlreadyExistException("Service name already exists");
        }
        ServiceCategory serviceCategory = serviceCategoryRepository.findByUuid(serviceCategoryUuid).orElseThrow(
                ()->new EntityNotFoundException("No category with the provided identifier")
        );
        service.setUuid(UuidGenerator.generateRandomString(12));
        service.setServiceCategory(serviceCategory);
        serviceRepository.save(service);
        return new ResponseEntity<>(new ApiResponse(true, "service saved"), HttpStatus.CREATED);
    }

    public ResponseEntity<PagedResponse> getServices(int pageNo, int pageSize){
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
        return new ResponseEntity<>(response,HttpStatus.OK );
    }
    public ResponseEntity<ApiResponse> requestService(String customerUuid, String serviceUuid, ServiceRequest serviceRequest){
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

        return new ResponseEntity<>(new ApiResponse(true, "service request placed"), HttpStatus.OK);
    }

}
