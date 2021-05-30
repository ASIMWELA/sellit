package com.sellit.api.controller;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.SigninRequest;
import com.sellit.api.payload.SigninResponse;
import com.sellit.api.service.AuthService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    @Transactional
    public ResponseEntity<SigninResponse> login(@RequestBody @Valid SigninRequest signinRequest){
        return authService.signin(signinRequest);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/block-user/{userUuid}")
    public ResponseEntity<ApiResponse> blockUser(@PathVariable @NonNull String userUuid){
        return authService.blockUserAccount(userUuid);
    }
    @Secured("ROLE_ADMIN")
    @PutMapping("/enable-user/{userUuid}")
    public ResponseEntity<ApiResponse> enableUser(@PathVariable @NonNull String userUuid){
        return authService.enableUser(userUuid);
    }
}
