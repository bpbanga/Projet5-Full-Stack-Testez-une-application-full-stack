package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.mapper.SessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        session = new Session();
        session.setId(1L);
        session.setName("Yoga Matin");

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Matin");
    }

    @Test
    void testFindById_Found() {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.findById("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(sessionDto);
    }

    @Test
    void testFindById_NotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindById_BadRequest() {
        ResponseEntity<?> response = sessionController.findById("invalid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testFindAll() {
        List<Session> sessions = List.of(session);
        List<SessionDto> dtos = List.of(sessionDto);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(dtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtos);
    }

    @Test
    void testCreate() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(sessionDto);
    }

    @Test
    void testDelete_Success() {
        when(sessionService.getById(1L)).thenReturn(session);

        ResponseEntity<?> response = sessionController.delete("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(sessionService, times(1)).delete(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(sessionService.getById(1L)).thenReturn(null);

        ResponseEntity<?> response = sessionController.delete("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
