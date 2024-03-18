package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRequestControllerUnitTest {
    ItemRequestService mockItemRequestService;
    ItemRequestController itemRequestController;

    ItemRequestDto itemRequestDto;
    ItemRequestOutputDto itemRequestOutputDto;
    ItemRequestWithAnswersDto itemRequestWithAnswersDto;

    LocalDateTime now;

    @BeforeAll
    void setUp() {
        now = LocalDateTime.now();

        itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();

        itemRequestOutputDto = ItemRequestOutputDto.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .created(now).build();

        itemRequestWithAnswersDto = ItemRequestWithAnswersDto.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .created(now)
                .requester(UserDto.builder().id(1L).build())
                .items(List.of(ItemDto.builder().id(1L).build())).build();

        mockItemRequestService = Mockito.mock(ItemRequestService.class);
        itemRequestController = new ItemRequestController(mockItemRequestService);
    }

    @Test
    void addItemRequestTest() {
        when(mockItemRequestService.addItemRequest(any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestOutputDto);
        assertEquals(itemRequestOutputDto, itemRequestController.addItemRequest(itemRequestDto, 1L));
    }

    @Test
    void getItemRequestsByUserIdTest() {
        when(mockItemRequestService.getItemRequestsByUserId(1L)).thenReturn(List.of(itemRequestWithAnswersDto));
        assertEquals(List.of(itemRequestWithAnswersDto), itemRequestController.getItemRequestsByUserId(1L));
    }

    @Test
    void getAllRequestsTest() {
        when(mockItemRequestService.getAllRequests(1L, 0, 20)).thenReturn(List.of(itemRequestWithAnswersDto));
        assertEquals(List.of(itemRequestWithAnswersDto), itemRequestController.getAllRequests(1L, 0, 20));
    }

    @Test
    void getItemRequestByIdTest() {
        when(mockItemRequestService.getItemRequestById(1L, 1L)).thenReturn(itemRequestWithAnswersDto);
        assertEquals(itemRequestWithAnswersDto, itemRequestController.getItemRequestById(1L, 1L));
    }
}
