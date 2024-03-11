package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ModelMapper mapper;
    private final ItemStorage itemStorage;

    public ItemDto createItem(ItemDto item, long userId) {
        return mapper.map(itemStorage.createItem(mapper.map(item, Item.class), userId),
                ItemDto.class);
    }

    public ItemDto updateItem(ItemDto item, long userId, long itemId) {
        return mapper.map(itemStorage.updateItem(mapper.map(item, Item.class),
                userId, itemId), ItemDto.class);
    }

    public List<ItemDto> getAllUserItems(long userId) {
        return itemStorage.getAllUserItems(userId)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long userid, long itemId) {
        return mapper.map(itemStorage.getItemById(itemId), ItemDto.class);

    }

    public List<ItemDto> getSearchItems(String text) {
        return itemStorage.getSearchItems(text)
                .stream()
                .map(item -> mapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}
