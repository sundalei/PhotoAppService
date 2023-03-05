package com.example.service;

import com.example.data.UserEntity;
import com.example.data.UsersRepository;
import com.example.shared.UserDto;
import com.example.ui.model.AlbumResponseModel;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

	private final UsersRepository usersRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final RestTemplate restTemplate;
	private final Environment environment;

	public UsersServiceImpl(UsersRepository usersRepository, 
			BCryptPasswordEncoder bCryptPasswordEncoder,
			RestTemplate restTemplate,
			Environment environment) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.restTemplate = restTemplate; 
		this.environment = environment;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {

		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		usersRepository.save(userEntity);

		return modelMapper.map(userEntity, UserDto.class);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);

		if (userEntity == null) {
			throw new UsernameNotFoundException(username);
		}

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				true,
				true,
				true,
				true,
				new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = usersRepository.findByEmail(email);
		
		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}
	
	@Override
	public UserDto getUserByUserId(String userId) {
		
		UserEntity userEntity = usersRepository.findByUserId(userId);
		
		if (userEntity == null) {
			throw new UsernameNotFoundException("User not found");
		}
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		
		final String albumsUrl = String.format(Objects.requireNonNull(environment.getProperty("albums.url")), userId);
		
		ResponseEntity<List<AlbumResponseModel>> albumsListResponse =
				restTemplate.exchange(albumsUrl,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<List<AlbumResponseModel>>() {
						});

		List<AlbumResponseModel> albums = albumsListResponse.getBody();
		
		userDto.setAlbums(albums);
		
		return userDto;
	}
}
