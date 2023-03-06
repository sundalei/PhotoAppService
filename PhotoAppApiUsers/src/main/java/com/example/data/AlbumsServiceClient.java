package com.example.data;

import com.example.ui.model.AlbumResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {
    
    @GetMapping("/users/{id}/albumss")
    List<AlbumResponseModel> getAlbums(@PathVariable("id") String id);
}
