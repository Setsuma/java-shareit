package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    void deleteUser(long userId);

    User updateUser(User user);

    User getUserById(long userId);

    List<User> getAllUsers();
}
