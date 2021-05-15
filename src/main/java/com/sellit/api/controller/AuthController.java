package com.sellit.api.controller;

import com.sellit.api.payload.SigninRequest;
import com.sellit.api.payload.SigninResponse;
import com.sellit.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<SigninResponse> login(@RequestBody @Valid SigninRequest signinRequest){
        return authService.signin(signinRequest);
    }
}
