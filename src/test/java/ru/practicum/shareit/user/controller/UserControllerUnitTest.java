package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerUnitTest {

    UserService mockUserService;
    UserController userController;

    UserDto userDto;
    UserDto updateUserDto;

    @BeforeAll
    void setUp() {
        userDto = UserDto.builder()
                .id(1)
                .name("user1")
                .email("user1@yandex.com")
                .build();

        updateUserDto = UserDto.builder()
                .id(1)
                .name("updatedUser")
                .email("updatedUser@yandex.com")
                .build();

        mockUserService = Mockito.mock(UserService.class);
        userController = new UserController(mockUserService);
    }

    @Test
    void createUserTest() {
        when(mockUserService.createUser(any(UserDto.class))).thenReturn(userDto);
        assertEquals(userDto, userController.createUser(userDto).getBody());
    }

    @Test
    void updateUserTest() {
        when(mockUserService.updateUser(any(UserDto.class), anyLong())).thenReturn(updateUserDto);
        assertEquals(updateUserDto, userController.updateUser(updateUserDto, updateUserDto.getId()).getBody());
    }

    @Test
    void deleteUserTest() {
        assertEquals("the user has been successfully deleted", userController.deleteUser(userDto.getId()).getBody());
    }

    @Test
    void getUserByIdTest() {
        when(mockUserService.getUserById(anyLong())).thenReturn(userDto);
        assertEquals(userDto, userController.getUserById(userDto.getId()).getBody());
    }

    @Test
    void getAllUsersTest() {
        when(mockUserService.getAllUsers()).thenReturn(List.of(userDto));
        assertEquals(List.of(userDto), userController.getAllUsers().getBody());
    }
}