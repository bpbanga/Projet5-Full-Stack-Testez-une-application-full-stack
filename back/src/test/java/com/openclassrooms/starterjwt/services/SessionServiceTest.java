package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exceptions.BadRequestException;
import com.openclassrooms.starterjwt.exceptions.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repositorys.SessionRepository;
import com.openclassrooms.starterjwt.repositorys.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");

        session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());
    }

    // ---------- CREATE ----------
    @Test
    void testCreate_ShouldSaveSession() {
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(sessionRepository, times(1)).save(session);
    }

    // ---------- DELETE ----------
    @Test
    void testDelete_ShouldCallRepository() {
        doNothing().when(sessionRepository).deleteById(1L);

        sessionService.delete(1L);

        verify(sessionRepository, times(1)).deleteById(1L);
    }

    // ---------- FIND ALL ----------
    @Test
    void testFindAll_ShouldReturnSessions() {
        when(sessionRepository.findAll()).thenReturn(List.of(session));

        List<Session> result = sessionService.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // ---------- GET BY ID ----------
    @Test
    void testGetById_ShouldReturnSession_WhenExists() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_ShouldReturnNull_WhenNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(1L);

        assertNull(result);
    }

    // ---------- UPDATE ----------
    @Test
    void testUpdate_ShouldSetIdAndSave() {
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session updated = new Session();
        updated.setUsers(new ArrayList<>());

        Session result = sessionService.update(1L, updated);

        assertEquals(1L, result.getId());
        verify(sessionRepository, times(1)).save(updated);
    }

    // ---------- PARTICIPATE ----------
    @Test
    void testParticipate_ShouldAddUser() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(1L, 1L);

        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipate_ShouldThrowNotFound_WhenSessionMissing() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void testParticipate_ShouldThrowNotFound_WhenUserMissing() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void testParticipate_ShouldThrowBadRequest_WhenAlreadyParticipate() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }

    // ---------- NO LONGER PARTICIPATE ----------
    @Test
    void testNoLongerParticipate_ShouldRemoveUser() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 1L);

        assertTrue(session.getUsers().isEmpty());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipate_ShouldThrowNotFound_WhenSessionMissing() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }

    @Test
    void testNoLongerParticipate_ShouldThrowBadRequest_WhenUserNotParticipate() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }
}

