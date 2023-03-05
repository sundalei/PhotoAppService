package com.example.ui.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.UsersService;
import com.example.shared.UserDto;
import com.example.ui.model.CreateUserRequestModel;
import com.example.ui.model.CreateUserResponseModel;
import com.example.ui.model.UserResponseModel;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {
	

    private final Environment env;
    private final UsersService usersService;

    public UsersController(Environment env, UsersService usersService) {
        this.env = env;
        this.usersService = usersService;
    }
    
    @GetMapping("/status/check")
    public String status() {
    	    	
        return "Working on port " + env.getProperty("local.server.port") + ", with ip = " + env.getProperty("gateway.ip");
    }
    
    @GetMapping(value = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UserResponseModel> getUser(@PathVariable String userId) {
    	
    	ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    	
    	UserDto userDto = usersService.getUserByUserId(userId);
    	
    	UserResponseModel returnValue = modelMapper.map(userDto, UserResponseModel.class);
    	
    	return ResponseEntity.ok(returnValue);
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid  @RequestBody CreateUserRequestModel userDetails) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto createdUser = usersService.createUser(userDto);

        CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
}
