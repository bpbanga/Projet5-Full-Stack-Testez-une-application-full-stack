package com.openclassrooms.starterjwt.services.mapper;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MapperHelper {

    private final TeacherService teacherService;
    private final UserService userService;

    public MapperHelper(TeacherService teacherService, UserService userService) {
        this.teacherService = teacherService;
        this.userService = userService;
    }

    @Named("mapTeacher")
    public Teacher mapTeacher(Long id) {
        return id != null ? teacherService.findById(id).orElse(null) : null;
    }

    @Named("mapUsers")
    public List<User> mapUsers(List<Long> ids) {
        if (ids == null) return Collections.emptyList();
        return ids.stream()
                .map(userService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Named("mapUserIds")
    public List<Long> mapUserIds(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream().map(User::getId).collect(Collectors.toList());
    }
}
