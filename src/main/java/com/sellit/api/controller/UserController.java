package com.sellit.api.controller;

import com.sellit.api.payload.ApiResponse;
import com.sellit.api.payload.PagedResponse;
import com.sellit.api.payload.user.UpdateUserAddressRequest;
import com.sellit.api.payload.user.UserUpdateRequest;
import com.sellit.api.service.UsersService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UsersService usersService;
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }
    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<PagedResponse> getUsers(@PositiveOrZero(message = "page number cannot be negative") @RequestParam(defaultValue = "0") Integer pageNo, @Positive @RequestParam(defaultValue = "10") Integer pageSize){
        return usersService.getUsers(pageNo, pageSize);
    }
    @PutMapping("/{userUuid}/update-user")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable String userUuid,@RequestBody @Valid UserUpdateRequest userUpdateRequest){
        return usersService.updateUserDetails(userUuid, userUpdateRequest);
    }
    @PutMapping("/{userUuid}/update-user-address")
    public ResponseEntity<ApiResponse> update(@PathVariable String userUuid,@RequestBody @Valid UpdateUserAddressRequest updateUserAddressRequest){
        return usersService.updateUserAddress(userUuid, updateUserAddressRequest);
    }
}
