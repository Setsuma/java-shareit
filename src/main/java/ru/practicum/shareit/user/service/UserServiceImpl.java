package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public UserDto createUser(UserDto userDto) {
        User user = mapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);
        return mapper.map(savedUser, UserDto.class);
    }

    public UserDto updateUser(UserDto userDto, long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не найден"));
        if (userDto.getName() != null) existingUser.setName(userDto.getName());
        if (userDto.getEmail() != null) existingUser.setEmail(userDto.getEmail());
        return mapper.map(userRepository.save(existingUser), UserDto.class);
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto getUserById(long userId) {
        return mapper.map(userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("err")), UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}