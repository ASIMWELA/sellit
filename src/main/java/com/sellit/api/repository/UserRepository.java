package com.sellit.api.repository;

import com.sellit.api.Entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<User> findByUuid(String uuid);
}
