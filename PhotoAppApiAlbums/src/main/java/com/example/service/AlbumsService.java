package com.example.service;

import java.util.List;

import com.example.data.AlbumEntity;

public interface AlbumsService {

	List<AlbumEntity> getAlbums(String userId);
}
