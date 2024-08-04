package com.spotify.catalogcontext.application;

import com.spotify.catalogcontext.application.dto.FavoriteSongDTO;
import com.spotify.catalogcontext.application.dto.ReadSongInfoDTO;
import com.spotify.catalogcontext.application.dto.SaveSongDTO;
import com.spotify.catalogcontext.application.dto.SongContentDTO;
import com.spotify.catalogcontext.application.mapper.SongContentMapper;
import com.spotify.catalogcontext.application.mapper.SongMapper;
import com.spotify.catalogcontext.domain.FavoriteId;
import com.spotify.catalogcontext.domain.Favourite;
import com.spotify.catalogcontext.domain.Song;
import com.spotify.catalogcontext.domain.SongContent;
import com.spotify.catalogcontext.repository.FavoriteRepository;
import com.spotify.catalogcontext.repository.SongContentRepository;
import com.spotify.catalogcontext.repository.SongRepository;
import com.spotify.infrastructure.service.dto.State;
import com.spotify.infrastructure.service.dto.StateBuilder;
import com.spotify.usercontext.ReadUserDTO;
import com.spotify.usercontext.application.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final SongContentRepository songContentRepository;
    private final SongMapper songMapper;
    private final SongContentMapper songContentMapper;
    private final UserService userService;

    private final FavoriteRepository favoriteRepository;

    public SongService(SongRepository songRepository, SongContentRepository songContentRepository,
                       SongMapper songMapper, SongContentMapper songContentMapper,
                       UserService userService,FavoriteRepository favoriteRepository) {
        this.songRepository = songRepository;
        this.songContentRepository = songContentRepository;
        this.songMapper = songMapper;
        this.songContentMapper = songContentMapper;
        this.userService = userService;
        this.favoriteRepository =favoriteRepository;
    }


    @Transactional
    public ReadSongInfoDTO create(SaveSongDTO saveSongDTO){
        Song song = songMapper.saveSongDTOToSong(saveSongDTO);
        Song savedSong = songRepository.save(song);

        SongContent songContent = songContentMapper.saveSongDTOToSong(saveSongDTO);
        songContent.setSong(savedSong);

        songContentRepository.save(songContent);
        return songMapper.songToReadSongInfoDTO(savedSong);
    }

    @Transactional(readOnly = true)
    public List<ReadSongInfoDTO> getAll() {

        List<ReadSongInfoDTO> allSongs = songRepository.findAll()
                .stream()
                .map(songMapper::songToReadSongInfoDTO)
                .toList();

        if(userService.isAuthenticated()) {
            return fetchFavoritesStatusForSongs(allSongs);
        }

        return allSongs;
    }

    public Optional<SongContentDTO> getOneByPublicId(UUID publicId){
        Optional<SongContent> songByPublicId = songContentRepository.findOneBySongPublicId(publicId);
        return songByPublicId.map(songContentMapper::songContentToSongContentDTO);
    }

    public List<ReadSongInfoDTO> search(String searchTerm) {
        List<ReadSongInfoDTO> searchedSongs = songRepository.findByTitleOrAuthorContaining(searchTerm)
                .stream()
                .map(songMapper::songToReadSongInfoDTO).toList();

        if(userService.isAuthenticated()) {
            return fetchFavoritesStatusForSongs(searchedSongs);
        } else {
            return searchedSongs;
        }
    }

    public State<FavoriteSongDTO, String> addOrRemoveFromFavorite(FavoriteSongDTO favoriteSongDTO, String email) {
        StateBuilder<FavoriteSongDTO, String> builder = State.builder();
        Optional<Song> songToLikeOpt = songRepository.findOneByPublicId(favoriteSongDTO.publicId());
        if (songToLikeOpt.isEmpty()) {
            return builder.forError("Song public id doesn't exist").build();
        }

        Song songToLike = songToLikeOpt.get();

        ReadUserDTO userWhoLikedSong = userService.getByEmail(email).orElseThrow();

        if (favoriteSongDTO.favorite()) {
            Favourite favorite = new Favourite();
            favorite.setSongPublicId(songToLike.getPublicId());
            favorite.setUserEmail(userWhoLikedSong.email());
            favoriteRepository.save(favorite);
        } else {
            FavoriteId favoriteId = new FavoriteId(songToLike.getPublicId(), userWhoLikedSong.email());
            favoriteRepository.deleteById(favoriteId);
            favoriteSongDTO = new FavoriteSongDTO(false, songToLike.getPublicId());
        }

        return builder.forSuccess(favoriteSongDTO).build();
    }

    public List<ReadSongInfoDTO> fetchFavoriteSongs(String email) {
        return songRepository.findAllFavoriteByUserEmail(email)
                .stream()
                .map(songMapper::songToReadSongInfoDTO)
                .toList();
    }

    private List<ReadSongInfoDTO> fetchFavoritesStatusForSongs(List<ReadSongInfoDTO> songs) {
        ReadUserDTO authenticatedUser = userService.getAuthenticatedUserFromSecurityContext();

        List<UUID> songPublicIds = songs.stream().map(ReadSongInfoDTO::getPublicId).toList();

        List<UUID> userFavoriteSongs = favoriteRepository.findAllByUserEmailAndSongPublicIdIn(authenticatedUser.email(), songPublicIds)
                .stream().map(Favourite::getSongPublicId).toList();

        return songs.stream().peek(song -> {
            if (userFavoriteSongs.contains(song.getPublicId())) {
                song.setFavorite(true);
            }
        }).toList();
    }

}
