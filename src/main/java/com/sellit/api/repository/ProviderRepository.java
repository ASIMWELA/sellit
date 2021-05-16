package com.sellit.api.repository;

import com.sellit.api.Entity.Provider;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProviderRepository extends PagingAndSortingRepository<Provider, Long> {

    Optional<Provider> findByUuid(String uuid);

}
