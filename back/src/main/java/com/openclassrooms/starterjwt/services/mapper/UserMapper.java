package com.openclassrooms.starterjwt.services.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, User> {

    @Override
    @Mapping(target = "password", ignore = true) 
    UserDto toDto(User entity);

    @Override
    User toEntity(UserDto dto);
}
