package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import com.openclassrooms.starterjwt.services.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFindById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById("1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.findById("1");

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void testFindById_InvalidId() {
        ResponseEntity<?> response = userController.findById("abc");

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void testDelete_UserAuthorized() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        ResponseEntity<?> response = userController.delete("1");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(userService).deleteUser(1L);
    }

    @Test
    void testDelete_UserUnauthorized() {
        User user = new User();
        user.setId(1L);
        user.setEmail("someone@example.com");

        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("other@example.com");

        ResponseEntity<?> response = userController.delete("1");

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        verify(userService, never()).deleteUser(1L);
    }

    @Test
    void testDelete_UserNotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.delete("1");

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void testDelete_InvalidId() {
        ResponseEntity<?> response = userController.delete("abc");

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}
