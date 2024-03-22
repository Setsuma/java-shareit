package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long userId, long itemId);

    List<ItemWithBookingAndCommentsDto> getAllUserItems(long userId, int from, int size);

    ItemWithBookingAndCommentsDto getItemById(long userId, long itemId);

    List<ItemDto> getSearchItems(String text, int from, int size);

    CommentOutputDto createComment(CommentDto commentDto, long userId, long itemId);
}