package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {
	
	private static Logger LOG = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);
	
	@Bean
	GlobalFilter secondPreFilter() {
		
		return (exchange, chain) -> {
			
			LOG.info("My second global pre-filter is executed...");
			 
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				
				LOG.info("My second global post-filter is executed...");
			}));
		};
	}
	
	@Bean
	GlobalFilter thirdPreFilter() {
		
		return (exchange, chain) -> {
			
			LOG.info("My third global pre-filter is executed...");
			 
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				
				LOG.info("My third global post-filter is executed...");
			}));
		};
	}
	
	@Bean
	GlobalFilter fourthPreFilter() {
		
		return (exchange, chain) -> {
			
			LOG.info("My fourth global pre-filter is executed...");
			 
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				
				LOG.info("My fourth global post-filter is executed...");
			}));
		};
	}
}
