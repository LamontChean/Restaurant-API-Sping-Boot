package com.restful.restaurant.api.controller;

import com.restful.restaurant.api.common.CommonResponse;
import com.restful.restaurant.api.dto.LoginRequest;
import com.restful.restaurant.api.exception.InvalidCredentialsException;
import com.restful.restaurant.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public CommonResponse<String> login(@RequestBody @Valid LoginRequest loginRequest) throws InvalidCredentialsException {

        return new CommonResponse<>(authService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }
}
