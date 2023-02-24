package com.example;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationHeaderFilter.class);
	
	private final Environment environment;
	
	public static class Config {
		// Put configuration properties here
	}

	public AuthorizationHeaderFilter(Environment environment) {
		super(Config.class);
		
		this.environment = environment;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			
			ServerHttpRequest request = exchange.getRequest();
			
			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				// Unauthorised
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");
			
			LOG.info("jwt token {}, start with blank? {}", jwt, jwt.startsWith(" "));
			
			if (!isJwtValid(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}
			
			return chain.filter(exchange);
		};
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		
		return response.setComplete();
	}
	
	private boolean isJwtValid(String jwt) {

		boolean returnValue = true;
		
		String subject = null;

		try {
			subject = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(jwt).getBody()
					.getSubject();
		} catch (Exception ex) {
			returnValue = false;
		}
		
		LOG.info("subject is {}", subject);
		
		if (subject == null || subject.isEmpty()) {
			returnValue = false;
		}

		return returnValue;
	}
	
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(environment.getProperty("token.secret"));
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
