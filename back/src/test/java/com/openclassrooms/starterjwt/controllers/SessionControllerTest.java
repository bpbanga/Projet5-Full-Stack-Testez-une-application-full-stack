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
        ResponseEntity<?> response = sessionController.findById("abc");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testFindById_InternalError() {
        when(sessionService.getById(1L)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<?> response = sessionController.findById("1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testFindAll_Success() {
        when(sessionService.findAll()).thenReturn(List.of(session));
        when(sessionMapper.toDto(List.of(session))).thenReturn(List.of(sessionDto));

        ResponseEntity<?> response = sessionController.findAll();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testFindAll_Error() {
        when(sessionService.findAll()).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<?> response = sessionController.findAll();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testCreate_Success() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreate_Error() {
        when(sessionMapper.toEntity(sessionDto)).thenThrow(new RuntimeException("Mapping error"));
        ResponseEntity<?> response = sessionController.create(sessionDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testUpdate_Success() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testUpdate_NotFound() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(null);

        ResponseEntity<?> response = sessionController.update("1", sessionDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdate_BadRequest() {
        ResponseEntity<?> response = sessionController.update("abc", sessionDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdate_Error() {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<?> response = sessionController.update("1", sessionDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
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

    @Test
    void testDelete_BadRequest() {
        ResponseEntity<?> response = sessionController.delete("abc");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDelete_Error() {
        when(sessionService.getById(1L)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<?> response = sessionController.delete("1");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testParticipate_Success() {
        ResponseEntity<?> response = sessionController.participate("1", "2");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).participate(1L, 2L);
    }

    @Test
    void testParticipate_BadRequest() {
        ResponseEntity<?> response = sessionController.participate("abc", "xyz");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testParticipate_Error() {
        doThrow(new RuntimeException("Service error")).when(sessionService).participate(1L, 2L);
        ResponseEntity<?> response = sessionController.participate("1", "2");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testNoLongerParticipate_Success() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(sessionService, times(1)).noLongerParticipate(1L, 2L);
    }

    @Test
    void testNoLongerParticipate_BadRequest() {
        ResponseEntity<?> response = sessionController.noLongerParticipate("abc", "xyz");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testNoLongerParticipate_Error() {
        doThrow(new RuntimeException("Service error")).when(sessionService).noLongerParticipate(1L, 2L);
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "2");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
