package com.example;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class MyPreFilter implements GlobalFilter, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(MyPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        LOG.info("My first Pre-filter is executed...");
        
        String requestPath = exchange.getRequest().getPath().toString();
        LOG.info("Request path = " + requestPath);
        
        HttpHeaders headers = exchange.getRequest().getHeaders();
        
        Set<String> headerNames = headers.keySet();
        
        headerNames.forEach((headerName) -> {
        	
        	String headerValue = headers.getFirst(headerName);
        	LOG.info(headerName + " " + headerValue);
        });
        
        
        return chain.filter(exchange);
    }

	@Override
	public int getOrder() {
		
		return 0;
	}
}
