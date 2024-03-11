package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item, long userId);

    Item updateItem(Item item, long userId, long itemId);

    List<Item> getAllUserItems(long userId);

    Item getItemById(long itemId);

    List<Item> getSearchItems(String text);
}
