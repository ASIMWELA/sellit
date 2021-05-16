package com.sellit.api.service;

import com.sellit.api.Entity.ServiceCategory;
import com.sellit.api.exception.EntityAlreadyExistException;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.repository.ProviderRepository;
import com.sellit.api.repository.ServiceCategoryRepository;
import com.sellit.api.repository.ServiceProviderRepository;
import com.sellit.api.repository.ServiceRepository;
import com.sellit.api.utils.UuidGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ServiceTransactions {

    final private ServiceCategoryRepository serviceCategoryRepository;
    final private ServiceRepository serviceRepository;


    public ServiceTransactions(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, ProviderRepository providerRepository, ServiceProviderRepository serviceProviderRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
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



}
