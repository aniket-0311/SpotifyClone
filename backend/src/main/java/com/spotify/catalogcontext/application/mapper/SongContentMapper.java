package com.spotify.catalogcontext.application.mapper;

import com.spotify.catalogcontext.application.dto.SaveSongDTO;
import com.spotify.catalogcontext.application.dto.SongContentDTO;
import com.spotify.catalogcontext.domain.SongContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SongContentMapper {

    @Mapping(source = "song.publicId", target = "publicId")
    SongContentDTO songContentToSongContentDTO(SongContent songContent);

    SongContent saveSongDTOToSong(SaveSongDTO songDTO);
}
