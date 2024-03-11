package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 1;

    public User createUser(User user) {
        checkEmail(user);
        user.setId(id);
        users.put(id, user);
        return users.get(id++);
    }

    public void deleteUser(long userId) {
        users.remove(userId);
    }

    public User updateUser(User user) {
        User updateUser = users.get(user.getId());
        if (updateUser != null) {
            checkEmail(user);
            if (user.getEmail() != null) updateUser.setEmail(user.getEmail());
            if (user.getName() != null) updateUser.setName(user.getName());
            return users.get(updateUser.getId());
        } else {
            throw new IdNotFoundException("Пользователь с данный Id не был найден");
        }
    }

    public User getUserById(long userId) {
        if (users.get(userId) == null) throw new IdNotFoundException("Пользователь с данный Id не был найден");
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkEmail(User user) {
        for (User existUser : users.values()) {
            if (existUser.getId() != user.getId() && user.getEmail() != null && existUser.getEmail().equals(user.getEmail())) {
                throw new EmailAlreadyExistsException("Пользователь с такой почтой уже существует");
            }
        }
    }
}
