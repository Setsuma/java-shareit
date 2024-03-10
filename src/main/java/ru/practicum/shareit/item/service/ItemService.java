package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long userId, long itemId);

    List<ItemDto> getAllUserItems(long userId);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getSearchItems(String text);
}
