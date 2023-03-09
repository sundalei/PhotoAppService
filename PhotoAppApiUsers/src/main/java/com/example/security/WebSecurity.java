package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.service.UsersService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends AbstractHttpConfigurer<WebSecurity, HttpSecurity> {

	private final Environment environment;

	private final UsersService userService;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurity(Environment environment, UsersService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.environment = environment;
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		http.addFilterBefore(getAuthenticationFilter(authenticationManager),
				UsernamePasswordAuthenticationFilter.class);
	}

	public WebSecurity webSecurity() {
		return new WebSecurity(environment, userService, bCryptPasswordEncoder);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
		    .authorizeHttpRequests((authorizeHttpRequests) -> {
//		    	authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll();
//		    	authorizeHttpRequests.requestMatchers(HttpMethod.GET, "/actuator/circuitbreakerevents").permitAll();
		    	authorizeHttpRequests.requestMatchers("/actuator/**").permitAll();
		        authorizeHttpRequests.requestMatchers("/users/**").permitAll();
		        authorizeHttpRequests.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll();
		});

		http.apply(webSecurity());

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.headers().frameOptions().disable();

		return http.build();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(userService);
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);

		return daoAuthenticationProvider;
	}

	private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment,
				authenticationManager);
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
}
