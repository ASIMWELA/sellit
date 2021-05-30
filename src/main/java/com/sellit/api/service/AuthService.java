package com.sellit.api.service;


import com.sellit.api.Entity.ServiceProvider;
import com.sellit.api.Entity.User;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.SigninRequest;
import com.sellit.api.payload.SigninResponse;
import com.sellit.api.payload.TokenPayload;
import com.sellit.api.repository.UserRepository;
import com.sellit.api.security.JwtTokenProvider;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    AuthenticationManager authenticationManager;
    JwtTokenProvider tokenProvider;
    UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    public ResponseEntity<SigninResponse> signin(SigninRequest signinRequest){
        log.info("Requesting to authenticate with the api");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getUserName().toLowerCase(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateJwtToken(authentication);

        log.info("Getting user information");
        User user =userRepository.findByUserName(tokenProvider.getUserNameFromToken(jwt).toLowerCase()).orElseThrow(
                ()->new EntityNotFoundException("Wrong credentials")
        );
        String serviceProviderUuid = null;
        if(user.isAProvider()){
            serviceProviderUuid = user.getProviderDetails().getServices().get(0).getUuid();
        }
        SigninResponse response = new SigninResponse();
        TokenPayload tokenPayload = TokenPayload.builder().build();
        tokenPayload.setAccessToken(jwt);
        tokenPayload.setType("Bearer");
        tokenPayload.setExpiresIn(tokenProvider.getExpirationMinutes(jwt));
        response.setUserData(user);
        response.setTokenPayload(tokenPayload);
        response.setServiceProviderUuid(serviceProviderUuid);
        log.info("Returning user information");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> blockUserAccount(String userUuid){
        User user = userRepository.findByUuid(userUuid).orElseThrow(
                ()-> new EntityNotFoundException("No user found with the identifier provided")
        );
        user.setEnabled(false);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "Account disabled succefully"), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> enableUser(String userUuid){
        User user = userRepository.findByUuid(userUuid).orElseThrow(
                ()-> new EntityNotFoundException("No user found with the identifier provided")
        );
        user.setEnabled(true);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "Account anabled"), HttpStatus.OK);
    }

}
