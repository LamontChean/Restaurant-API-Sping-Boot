package com.restful.restaurant.api.service;

import com.restful.restaurant.api.exception.InvalidCredentialsException;
import com.restful.restaurant.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${auth.username}")
    private String validUsername;

    @Value("${auth.password}")
    private String validPassword;

    public String login(String username, String password) throws InvalidCredentialsException {
        if (validUsername.equals(username) && validPassword.equals(password))
            return jwtUtil.generateToken(username);

        throw new InvalidCredentialsException();
    }
}
