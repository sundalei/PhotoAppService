package com.example.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ui.model.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {
	
	public static final Logger LOG = LoggerFactory.getLogger(AlbumsServiceClient.class);
    
    @GetMapping("/users/{id}/albums")
    @CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
    List<AlbumResponseModel> getAlbums(@PathVariable("id") String id);

    default List<AlbumResponseModel> getAlbumsFallback(String id, Throwable exception) {
    	
    	LOG.info("Param = {}", id);
    	LOG.info("Exception took place: " + exception.getMessage());
    	
        return List.of();
    }
}
