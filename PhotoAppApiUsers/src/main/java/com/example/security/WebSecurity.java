package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.service.UsersService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurity {

	private final Environment environment;

	private final UsersService userService;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurity(Environment environment, UsersService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.environment = environment;
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// Configure AuthenticationManagerBuilder
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);

		authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests((authorizeHttpRequests) -> {

					authorizeHttpRequests.requestMatchers("/actuator/**").permitAll();
					authorizeHttpRequests.requestMatchers("/users/**").permitAll();
					authorizeHttpRequests.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
							.permitAll();
				});

		http.addFilter(new AuthorizationFilter(authenticationManager, environment));
		http.addFilter(getAuthenticationFilter(authenticationManager));
		http.authenticationManager(authenticationManager);
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.headers(headers -> headers.frameOptions().disable());

		return http.build();
	}

	private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment,
				authenticationManager);
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}

}
