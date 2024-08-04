package com.spotify.usercontext.mapper;

import com.spotify.usercontext.ReadUserDTO;
import com.spotify.usercontext.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ReadUserDTO readUserDTOToUser(User user);
}
