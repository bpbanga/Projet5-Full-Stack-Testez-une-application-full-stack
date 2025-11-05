package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repositorys.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("Alice");
        teacher1.setLastName("Martin");

        teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setFirstName("Bob");
        teacher2.setLastName("Durand");
    }

    // ---------- findAll ----------
    @Test
    void testFindAll_ShouldReturnListOfTeachers() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher1, teacher2));

        List<Teacher> result = teacherService.findAll();

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        assertEquals("Bob", result.get(1).getFirstName());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_ShouldReturnEmptyList_WhenNoTeachers() {
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        List<Teacher> result = teacherService.findAll();

        assertTrue(result.isEmpty());
        verify(teacherRepository, times(1)).findAll();
    }

    // ---------- findById ----------
    @Test
    void testFindById_ShouldReturnTeacher_WhenExists() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        Optional<Teacher> result = teacherService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_ShouldReturnEmpty_WhenNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Teacher> result = teacherService.findById(99L);

        assertTrue(result.isEmpty());
        verify(teacherRepository, times(1)).findById(99L);
    }
}
