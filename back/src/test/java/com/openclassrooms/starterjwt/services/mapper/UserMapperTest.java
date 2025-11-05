package com.openclassrooms.starterjwt.services.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testToDto() {
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");  
        user.setLastName("Smith");          
        user.setFirstName("Alice");        
        user.setAdmin(false);               
        user.setPassword("secret");

        UserDto dto = userMapper.toDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("Smith", dto.getLastName());
        assertEquals("Alice", dto.getFirstName());
        assertEquals(false, dto.isAdmin());
        assertNull(dto.getPassword()); 
    }

    @Test
    void testToEntity() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("bob@example.com");    
        dto.setLastName("Doe");             
        dto.setFirstName("Bob");            
        dto.setAdmin(true);                 
        dto.setPassword("password123");

        User entity = userMapper.toEntity(dto);

        assertEquals(1L, entity.getId());
        assertEquals("bob@example.com", entity.getEmail());
        assertEquals("Doe", entity.getLastName());
        assertEquals("Bob", entity.getFirstName());
        assertTrue(entity.isAdmin());
        assertEquals("password123", entity.getPassword());
    }
}
