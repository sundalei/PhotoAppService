package com.example;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import feign.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PhotoAppApiUsersApplication {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PhotoAppApiUsersApplication.class);

    private final Environment environment;

    public PhotoAppApiUsersApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiUsersApplication.class, args);
	}

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
    	return new RestTemplate();
    }
    
    @Bean
    @Profile("!production")
    Logger.Level feignDefaultLoggerLevel() {
    	return Logger.Level.FULL;
    }

    @Bean
    @Profile("production")
    Logger.Level feignProductionLoggerLevel() {
        return Logger.Level.NONE;
    }

    @Bean
    @Profile("production")
    String createProductionBean() {
        LOG.info("Production bean created. myapplication.environment = "
                + environment.getProperty("myapplication.environment"));
        return "Production bean";
    }

    @Bean
    @Profile("!production")
    String createNotProductionBean() {
        LOG.info("Not production bean created. myapplication.environment = "
                + environment.getProperty("myapplication.environment"));
        return "Not production bean";
    }

    @Bean
    @Profile("default")
    String createDevelopmentBean() {
        LOG.info("Development bean created. myapplication.environment = "
                + environment.getProperty("myapplication.environment"));
        return "Development bean";
    }

}
