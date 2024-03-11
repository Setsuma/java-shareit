package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ModelMapper mapper;

    public UserDto createUser(UserDto userDto) {
        return mapper.map(userStorage.createUser(mapper.map(userDto, User.class)),
                UserDto.class);
    }

    public UserDto updateUser(UserDto userDto, long userId) {
        userDto.setId(userId);
        return mapper.map(userStorage.updateUser(mapper.map(userDto, User.class)),
                UserDto.class);
    }

    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    public UserDto getUserById(long userId) {
        return mapper.map(userStorage.getUserById(userId), UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}