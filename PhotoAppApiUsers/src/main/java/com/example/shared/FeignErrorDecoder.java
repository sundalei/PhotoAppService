package com.example.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

	private static final Logger LOG = LoggerFactory.getLogger(FeignErrorDecoder.class);
	
	private final Environment environment;

	public FeignErrorDecoder(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Exception decode(String methodKey, Response response) {

		LOG.info("method key {}", methodKey);

		switch (response.status()) {
		case 400:
			break;
		case 404:
			if (methodKey.contains("getAlbums")) {
				String message = environment.getProperty("albums.exception");
				return new ResponseStatusException(HttpStatus.valueOf(response.status()), message);
			}
			break;
		default:
			return new Exception(response.reason());
		}
		return null;
	}

}
