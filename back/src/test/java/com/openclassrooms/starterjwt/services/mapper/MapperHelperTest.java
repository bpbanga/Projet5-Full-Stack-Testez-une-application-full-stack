package com.openclassrooms.starterjwt.services.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
class MapperHelperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MapperHelper mapperHelper;

    @Test
    void testMapTeacher_withValidId() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherService.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = mapperHelper.mapTeacher(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testMapTeacher_withNullId() {
        Teacher result = mapperHelper.mapTeacher(null);
        assertNull(result);
    }

    @Test
    void testMapUsers_withValidIds() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(userService.findById(1L)).thenReturn(Optional.of(user1));
        when(userService.findById(2L)).thenReturn(Optional.of(user2));

        List<User> result = mapperHelper.mapUsers(List.of(1L, 2L));
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(user1, user2)));
    }

    @Test
    void testMapUsers_withEmptyOrNullList() {
        assertTrue(mapperHelper.mapUsers(null).isEmpty());
        assertTrue(mapperHelper.mapUsers(Collections.emptyList()).isEmpty());
    }

    @Test
    void testMapUserIds() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        List<Long> ids = mapperHelper.mapUserIds(List.of(user1, user2));
        assertEquals(List.of(1L, 2L), ids);
    }

    @Test
    void testMapUserIds_withNullList() {
        assertTrue(mapperHelper.mapUserIds(null).isEmpty());
    }
}

