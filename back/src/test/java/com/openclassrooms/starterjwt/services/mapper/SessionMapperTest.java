package com.openclassrooms.starterjwt.services.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;

@SpringBootTest
class SessionMapperTest {

    @Autowired
    private SessionMapper sessionMapper;

    @MockBean
    private MapperHelper mapperHelper;

    @Test
    void testToEntity() {
        SessionDto dto = new SessionDto();
        dto.setTeacher_id(1L);
        dto.setUsers(List.of(10L, 20L));

        Teacher teacher = new Teacher();
        teacher.setId(1L);

        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);

        when(mapperHelper.mapTeacher(1L)).thenReturn(teacher);
        when(mapperHelper.mapUsers(List.of(10L, 20L))).thenReturn(List.of(user1, user2));

        Session entity = sessionMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(teacher, entity.getTeacher());
        assertEquals(2, entity.getUsers().size());
    }

    @Test
    void testToDto() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);

        Session entity = new Session();
        entity.setTeacher(teacher);
        entity.setUsers(List.of(user1, user2));

        when(mapperHelper.mapUserIds(entity.getUsers())).thenReturn(List.of(10L, 20L));

        SessionDto dto = sessionMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getTeacher_id());
        assertEquals(List.of(10L, 20L), dto.getUsers());
    }
}

