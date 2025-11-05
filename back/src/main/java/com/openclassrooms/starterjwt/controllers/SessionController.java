package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.mapper.SessionMapper;
import com.openclassrooms.starterjwt.services.SessionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur de gestion des sessions.
 */
@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class SessionController {

    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    public SessionController(SessionService sessionService, SessionMapper sessionMapper) {
        this.sessionService = sessionService;
        this.sessionMapper = sessionMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        log.debug("Fetching session with id={}", id);
        try {
            long sessionId = Long.parseLong(id);
            Session session = sessionService.getById(sessionId);

            if (session == null) {
                log.warn("Session with id={} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
            }

            log.debug("Session with id={} found", id);
            return ResponseEntity.ok(sessionMapper.toDto(session));

        } catch (NumberFormatException e) {
            log.error("Invalid session id format: {}", id);
            return ResponseEntity.badRequest().body("Invalid session id format");
        } catch (Exception e) {
            log.error("Error fetching session {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error fetching session");
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        log.debug("Fetching all sessions");
        try {
            List<Session> sessions = sessionService.findAll();
            return ResponseEntity.ok(sessionMapper.toDto(sessions));
        } catch (Exception e) {
            log.error("Error fetching sessions: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error fetching sessions");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SessionDto sessionDto) {
        log.debug("Creating new session: {}", sessionDto);
        try {
            Session createdSession = sessionService.create(sessionMapper.toEntity(sessionDto));
            log.debug("Session created successfully with id={}", createdSession.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(sessionMapper.toDto(createdSession));
        } catch (Exception e) {
            log.error("Error creating session: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error creating session");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody SessionDto sessionDto) {
        log.debug("Updating session id={} with data={}", id, sessionDto);
        try {
            long sessionId = Long.parseLong(id);
            Session updated = sessionService.update(sessionId, sessionMapper.toEntity(sessionDto));

            if (updated == null) {
                log.warn("Session with id={} not found for update", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
            }

            log.debug("Session with id={} updated successfully", id);
            return ResponseEntity.ok(sessionMapper.toDto(updated));

        } catch (NumberFormatException e) {
            log.error("Invalid session id format: {}", id);
            return ResponseEntity.badRequest().body("Invalid session id format");
        } catch (Exception e) {
            log.error("Error updating session {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error updating session");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        log.debug("Deleting session with id={}", id);
        try {
            long sessionId = Long.parseLong(id);
            Session session = sessionService.getById(sessionId);

            if (session == null) {
                log.warn("Session with id={} not found for deletion", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
            }

            sessionService.delete(sessionId);
            log.debug("Session with id={} deleted successfully", id);
            return ResponseEntity.noContent().build();

        } catch (NumberFormatException e) {
            log.error("Invalid session id format: {}", id);
            return ResponseEntity.badRequest().body("Invalid session id format");
        } catch (Exception e) {
            log.error("Error deleting session {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error deleting session");
        }
    }

    @PostMapping("/{id}/participate/{userId}")
    public ResponseEntity<?> participate(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        log.debug("User {} participates in session {}", userId, id);
        try {
            sessionService.participate(Long.parseLong(id), Long.parseLong(userId));
            log.debug("User {} successfully added to session {}", userId, id);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            log.error("Invalid id or userId format: {}, {}", id, userId);
            return ResponseEntity.badRequest().body("Invalid id format");
        } catch (Exception e) {
            log.error("Error while user {} participates in session {}: {}", userId, id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error adding participant");
        }
    }

    @DeleteMapping("/{id}/participate/{userId}")
    public ResponseEntity<?> noLongerParticipate(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        log.debug("User {} cancels participation in session {}", userId, id);
        try {
            sessionService.noLongerParticipate(Long.parseLong(id), Long.parseLong(userId));
            log.debug("User {} successfully removed from session {}", userId, id);
            return ResponseEntity.ok().build();
        } catch (NumberFormatException e) {
            log.error("Invalid id or userId format: {}, {}", id, userId);
            return ResponseEntity.badRequest().body("Invalid id format");
        } catch (Exception e) {
            log.error("Error while removing user {} from session {}: {}", userId, id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error removing participant");
        }
    }
}
