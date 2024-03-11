package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final UserStorage userStorage;
    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 1;

    public Item createItem(Item item, long userId) {
        item.setOwner(userStorage.getUserById(userId));
        item.setId(id);
        items.put(id, item);
        return items.get(id++);
    }

    public Item updateItem(Item item, long userId, long itemId) {
        Item updateItem = items.get(itemId);
        if (updateItem != null && updateItem.getOwner().getId() == userId) {
            if (item.getName() != null) updateItem.setName(item.getName());
            if (item.getDescription() != null) updateItem.setDescription(item.getDescription());
            if (item.getAvailable() != null) updateItem.setAvailable(item.getAvailable());
        } else {
            throw new IdNotFoundException("Вещь пользователя с данным Id не найдена");
        }
        return updateItem;
    }

    public Item getItemById(long itemId) {
        if (items.get(itemId) == null) throw new IdNotFoundException("Вещь с данный Id не был найдена");
        return items.get(itemId);
    }

    public List<Item> getAllUserItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    public List<Item> getSearchItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return items.values().stream()
                .filter(item -> (StringUtils.containsIgnoreCase(item.getName(), text)
                        || StringUtils.containsIgnoreCase(item.getDescription(), text))
                        && (item.getAvailable()))
                .collect(Collectors.toList());
    }
}