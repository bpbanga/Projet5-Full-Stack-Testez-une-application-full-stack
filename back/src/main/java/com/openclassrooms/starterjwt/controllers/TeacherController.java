package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;


    public TeacherController(TeacherService teacherService,
                             TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
        this.teacherService = teacherService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        try {
            Optional<Teacher> teacherOpt = this.teacherService.findById(Long.valueOf(id));

            if (teacherOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Teacher teacher = teacherOpt.get();
            return ResponseEntity.ok().body(this.teacherMapper.toDto(teacher));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<Teacher> teachers = this.teacherService.findAll();

        return ResponseEntity.ok().body(this.teacherMapper.toDto(teachers));
    }
}
