package com.example.hotelswebapp.services;

import com.example.hotelswebapp.entity.ERole;
import com.example.hotelswebapp.entity.UserEntity;
import com.example.hotelswebapp.exception.UsernameAlreadyExistsException;
import com.example.hotelswebapp.repos.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByLogin() {
        String login = "testUser";
        UserEntity user = new UserEntity();
        user.setLogin(login);

        when(userRepo.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.findByLogin(login);
        assertTrue(result.isPresent());
        assertEquals(login, result.get().getLogin());
    }

    @Test
    void testSaveUser() throws UsernameAlreadyExistsException {
        UserEntity user = new UserEntity();
        user.setLogin("testUser");
        user.setPassword("password");

        when(userRepo.findByLogin(anyString())).thenReturn(Optional.empty());

        userService.saveUser(user);

        verify(userRepo).save(user);
    }

    @Test
    void testSaveUser_UsernameAlreadyExists() {
        UserEntity user = new UserEntity();
        user.setLogin("existingUser");

        when(userRepo.findByLogin(anyString())).thenReturn(Optional.of(new UserEntity()));

        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.saveUser(user));
    }

    @Test
    void testIsUsernameUnique() {
        String username = "testUser";
        when(userRepo.findByLogin(username)).thenReturn(Optional.empty());

        assertTrue(userService.isUsernameUnique(username));
    }

    @Test
    void testGetAuthUser() {
        String login = "authUser";
        UserEntity user = new UserEntity();
        user.setLogin(login);

        when(userRepo.findByLogin(login)).thenReturn(Optional.of(user));

        UserEntity result = userService.getAuthUser(login);
        assertEquals(login, result.getLogin());
    }

    @Test
    void testUpdateRole() {
        int userId = 1;
        String newRole = "ADMIN";
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setRole(ERole.USER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        userService.updateRole(userId, newRole);

        verify(userRepo).findById(userId);
        verify(userRepo).save(any(UserEntity.class));
    }

    @Test
    void testDeleteUser() {
        int id = 1;
        userService.deleteUser(id);
        verify(userRepo).deleteById(id);
    }
}
