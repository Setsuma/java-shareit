package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.AdvanceInfo;
import ru.practicum.shareit.user.dto.BasicInfo;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(BasicInfo.class) @RequestBody UserDto user) {
       return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated(AdvanceInfo.class) @RequestBody UserDto user,
                                              @PathVariable long userId) {
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userService.getAllUsers();
    }
}