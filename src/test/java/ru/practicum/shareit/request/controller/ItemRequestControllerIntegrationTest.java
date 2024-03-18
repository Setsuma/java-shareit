package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRequestControllerIntegrationTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    User requester;
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
    }

    @SneakyThrows
    @Test
    void addItemRequestTest() {
        when(itemRequestService.addItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestOutputDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutputDto.getDescription()), String.class));
    }

    @SneakyThrows
    @Test
    void getItemRequestsByUserIdTest() {
        when(itemRequestService.getItemRequestsByUserId(anyLong()))
                .thenReturn(List.of(itemRequestWithAnswersDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestWithAnswersDto))));
        verify(itemRequestService, times(1)).getItemRequestsByUserId(1L);


    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestWithAnswersDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(List.of(itemRequestWithAnswersDto))));
        verify(itemRequestService, times(1)).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestWithAnswersDto);

        mockMvc.perform(get("/requests/{requestId}", itemRequestWithAnswersDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(itemRequestWithAnswersDto)));
        verify(itemRequestService, times(1)).getItemRequestById(anyLong(), anyLong());
    }

}
