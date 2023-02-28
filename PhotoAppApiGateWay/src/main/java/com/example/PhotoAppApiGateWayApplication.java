package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class PhotoAppApiGateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiGateWayApplication.class, args);
	}

	@Bean
	public HttpExchangeRepository httpTraceRepository() {
		return new InMemoryHttpExchangeRepository();
	}

}
