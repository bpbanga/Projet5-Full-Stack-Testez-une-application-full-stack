package com.openclassrooms.starterjwt.services.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MapperHelper.class})
public interface SessionMapper extends EntityMapper<SessionDto, Session> {

    // DTO -> Entity
    @Mapping(target = "teacher", source = "teacher_id", qualifiedByName = "mapTeacher")
    @Mapping(target = "users", source = "users", qualifiedByName = "mapUsers")
    Session toEntity(SessionDto dto);

    // Entity -> DTO
    @Mapping(target = "teacher_id", source = "teacher.id")
    @Mapping(target = "users", source = "users", qualifiedByName = "mapUserIds")
    SessionDto toDto(Session entity);
}
