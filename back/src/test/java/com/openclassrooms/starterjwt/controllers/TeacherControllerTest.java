package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.mapper.TeacherMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
    }

    @Test
    void testFindById_Found() {
        when(teacherService.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        ResponseEntity<?> response = teacherController.findById("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(teacherDto);
    }

    @Test
    void testFindById_NotFound() {
        when(teacherService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = teacherController.findById("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testFindById_BadRequest() {
        ResponseEntity<?> response = teacherController.findById("invalid-id");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testFindAll() {
        List<Teacher> teachers = List.of(teacher);
        List<TeacherDto> teacherDtos = List.of(teacherDto);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        ResponseEntity<?> response = teacherController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(teacherDtos);
    }
}
