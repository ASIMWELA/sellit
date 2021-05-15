package com.sellit.api.repository;

import com.sellit.api.Entity.Provider;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProviderRepository extends PagingAndSortingRepository<Provider, Long> {
}
