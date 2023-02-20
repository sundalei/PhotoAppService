package com.example.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.shared.UserDto;

public interface UsersService extends UserDetailsService {

    UserDto createUser(UserDto userDetails);
    
    UserDto getUserDetailsByEmail(String email);
}
