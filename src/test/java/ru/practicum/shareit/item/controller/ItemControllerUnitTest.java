package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemControllerUnitTest {
    ItemService mockItemService;
    ItemController itemController;

    ItemDto itemDto;
    ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto;
    CommentDto commentDto;
    CommentOutputDto commentOutputDto;

    @BeforeAll
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        itemWithBookingAndCommentsDto = ItemWithBookingAndCommentsDto.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .lastBooking(BookingForItemDto.builder().build())
                .nextBooking(BookingForItemDto.builder().build())
                .comments(List.of(CommentOutputDto.builder().build()))
                .build();

        commentDto = CommentDto.builder()
                .text("comment")
                .build();

        commentOutputDto = CommentOutputDto.builder()
                .id(1L)
                .text(commentDto.getText())
                .authorName("author")
                .created(LocalDateTime.now())
                .build();

        mockItemService = Mockito.mock(ItemService.class);
        itemController = new ItemController(mockItemService);
    }

    @Test
    void createItem() {
        when(mockItemService.createItem(any(ItemDto.class), anyLong())).thenReturn(itemDto);
        assertEquals(itemDto, itemController.createItem(itemDto, 1L).getBody());
    }

    @Test
    void updateItem() {
        ItemDto updatedItem = itemDto.toBuilder().name("updatedName").build();

        when(mockItemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(updatedItem);
        assertEquals(updatedItem, itemController.updateItem(itemDto, 1L, 1L).getBody());
    }

    @Test
    void getItemById() {
        when(mockItemService.getItemById(anyLong(), anyLong())).thenReturn(itemWithBookingAndCommentsDto);
        assertEquals(itemWithBookingAndCommentsDto, itemController.getItemById(1L, 1L).getBody());
    }

    @Test
    void getAllUserItems() {
        when(mockItemService.getAllUserItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemWithBookingAndCommentsDto));
        assertEquals(List.of(itemWithBookingAndCommentsDto), itemController.getAllUserItems(1L, 0, 20).getBody());
    }

    @Test
    void getSearchItems() {
        when(mockItemService.getSearchItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        assertEquals(List.of(itemDto), itemController.getSearchItems("description", 0, 20).getBody());
    }

    @Test
    void createComment() {
        when(mockItemService.createComment(any(CommentDto.class), anyLong(), anyLong())).thenReturn(commentOutputDto);
        assertEquals(commentOutputDto, itemController.createComment(commentDto, 1L, 1L).getBody());
    }
}