package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.AdvanceInfo;
import ru.practicum.shareit.user.dto.BasicInfo;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated(BasicInfo.class) @RequestBody UserDto user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Validated(AdvanceInfo.class) @RequestBody UserDto user,
                                              @PathVariable int userId) {
        return ResponseEntity.ok(userService.updateUser(user, userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("the user has been successfully deleted");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}