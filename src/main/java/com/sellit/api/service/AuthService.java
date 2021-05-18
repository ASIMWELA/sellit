package com.sellit.api.service;


import com.sellit.api.Entity.User;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.payload.SigninRequest;
import com.sellit.api.payload.SigninResponse;
import com.sellit.api.payload.TokenPayload;
import com.sellit.api.repository.UserRepository;
import com.sellit.api.security.JwtTokenProvider;
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
public class AuthService {

    final AuthenticationManager authenticationManager;
    final JwtTokenProvider tokenProvider;
    final UserRepository userRepository;

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
                ()->new EntityNotFoundException("User not found with the name")
        );
        SigninResponse response = new SigninResponse();
        TokenPayload tokenPayload = TokenPayload.builder().build();
        tokenPayload.setAccessToken(jwt);
        tokenPayload.setType("Bearer");
        tokenPayload.setExpiresIn(tokenProvider.getExpirationMinutes(jwt));
        response.setUserData(user);
        response.setTokenPayload(tokenPayload);
        log.info("Returning user information");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
