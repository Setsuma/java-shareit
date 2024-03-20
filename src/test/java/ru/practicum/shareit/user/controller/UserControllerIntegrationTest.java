package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    UserDto userDto1;
    UserDto userDto2;
    UserDto updateUserDto;

    @BeforeAll
    void setUp() {
        userDto1 = UserDto.builder()
                .id(1)
                .name("user1")
                .email("user1@yandex.com")
                .build();

        userDto2 = UserDto.builder()
                .id(2)
                .name("user2")
                .email("user2@yandex.com")
                .build();

        updateUserDto = UserDto.builder()
                .id(1)
                .name("updatedUser")
                .email("updatedUser@yandex.com")
                .build();
    }

    @SneakyThrows
    @Test
    void createUser_WhenAllAreOk_ThenReturnSavedUserDto() {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect((jsonPath("$.email").value(userDto1.getEmail())));
    }

    @SneakyThrows
    @Test
    void createUser_WhenEmptyName_ThenThrowValidationException() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1.toBuilder().name("").build()))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @SneakyThrows
    @Test
    void createUser_WhenWrongEmail_ThenThrowValidationException() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1.toBuilder().email("").build()))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @SneakyThrows
    @Test
    void createUser_WhenEmptyEmail_ThenThrowValidationException() {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1.toBuilder().email("wrongEmail").build()))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }


    @SneakyThrows
    @Test
    void updateUser_whenAllIsOk_ThenReturnUpdatedUserDto() {
        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(updateUserDto);
        mockMvc.perform(patch("/users/{userId}", updateUserDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(updateUserDto.getName()))
                .andExpect((jsonPath("$.email").value(updateUserDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void updateUser_whenWrongEmail_ThenThrowValidationException() {
        mockMvc.perform(patch("/users/{userId}", updateUserDto.getId())
                        .content(objectMapper.writeValueAsString(userDto1.toBuilder().email("wrongEmail").build()))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation error"));
    }

    @SneakyThrows
    @Test
    void deleteUserFromDB_whenAllIsOk_ThenReturnOk() {
        mockMvc.perform(delete("/users/{id}", userDto1.getId()))

                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(userDto1.getId());
    }

    @SneakyThrows
    @Test
    void getAllUsersFromStorage_whenAllIsOk_thenResponseStatusOkWithUserCollection() {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto1.getName()))
                .andExpect((jsonPath("$[0].email").value(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id").value(userDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(userDto2.getName()))
                .andExpect((jsonPath("$[1].email").value(userDto2.getEmail())));
    }

    @SneakyThrows
    @Test
    void getUserById() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto1);
        mockMvc.perform(get("/users/{userId}", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect((jsonPath("$.email").value(userDto1.getEmail())));
    }


}
