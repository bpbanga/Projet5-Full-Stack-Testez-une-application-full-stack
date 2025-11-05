package com.openclassrooms.starterjwt.services.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

@SpringBootTest
class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    void testMapping() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Mr. Smith");

        TeacherDto dto = teacherMapper.toDto(teacher);
        assertEquals("Mr. Smith", dto.getFirstName());

        Teacher entity = teacherMapper.toEntity(dto);
        assertEquals("Mr. Smith", entity.getFirstName());
    }
}
