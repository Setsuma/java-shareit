package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    void deleteUser(long userId);

    UserDto updateUser(UserDto userDto, long userId);

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();
}
