package com.sellit.api.security;

import com.sellit.api.Entity.User;
import com.sellit.api.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailsServiceImpl implements UserDetailsService
{

    UserRepository userRepository;
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(userName.toLowerCase()).orElseThrow(
                ()->new EntityNotFoundException("Wrong credentials")
        );

        return UserDetailsImpl.build(user);
    }
}
