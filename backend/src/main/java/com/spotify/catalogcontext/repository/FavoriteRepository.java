package com.spotify.catalogcontext.repository;

import com.spotify.catalogcontext.domain.FavoriteId;
import com.spotify.catalogcontext.domain.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favourite, FavoriteId> {
    List<Favourite> findAllByUserEmailAndSongPublicIdIn(String email, List<UUID> songPublicIds);
}
