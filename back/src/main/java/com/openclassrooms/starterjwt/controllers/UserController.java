package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.mapper.UserMapper;
import com.openclassrooms.starterjwt.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        try {
            Long userId = Long.valueOf(id);
            Optional<User> optionalUser = userService.findById(userId);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(userMapper.toDto(optionalUser.get()));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        try {
            Long userId = Long.valueOf(id);
            Optional<User> optionalUser = userService.findById(userId);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = optionalUser.get();

            UserDetails userDetails = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            if (!Objects.equals(userDetails.getUsername(), user.getEmail())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            userService.deleteUser(userId);
            return ResponseEntity.ok().build();

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
