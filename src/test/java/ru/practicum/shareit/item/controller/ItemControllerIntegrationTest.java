package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemControllerIntegrationTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    ItemDto itemDto;
    Item item;
    ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto;
    User owner;
    User booker;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("owner");
        owner.setEmail("owner@yandex.ru");

        booker = new User();
        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("booker@yandex.ru");

        request = new ItemRequest();
        request.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setDescription("description1");
        item.setAvailable(true);
        item.setRequest(request);
        item.setOwner(owner);

        itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(request.getId());

        itemWithBookingAndCommentsDto = new ItemWithBookingAndCommentsDto();
        itemWithBookingAndCommentsDto.setId(item.getId());
        itemWithBookingAndCommentsDto.setName(item.getName());
        itemWithBookingAndCommentsDto.setDescription(item.getDescription());
        itemWithBookingAndCommentsDto.setAvailable(item.getAvailable());
        itemWithBookingAndCommentsDto.setLastBooking(null);
        itemWithBookingAndCommentsDto.setNextBooking(null);
        itemWithBookingAndCommentsDto.setRequestId(request.getId());
        itemWithBookingAndCommentsDto.setComments(new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void testGetItemByIdForOwner() {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemWithBookingAndCommentsDto);

        mockMvc.perform(get("/items/{id}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testGetAll() {
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemWithBookingAndCommentsDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestId", is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testSearchItemsByText() {
        when(itemService.getSearchItems("found one item", 0, 10))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "found one item")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        when(itemService.getSearchItems("items not found", 0, 10))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "items not found")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }


    @SneakyThrows
    @Test
    void testAdd() {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequest().getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }


    @SneakyThrows
    @Test
    void testUpdate_whenAllAreOk_aAndReturnUpdatedItem() {
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdate_whenAllAreNotOk_aAndReturnExceptionNotFoundRecordInBD() {
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addCommentToItem_whenAllIsOk_returnSavedComment() {
        CommentOutputDto commentOutputDto = new CommentOutputDto();
        commentOutputDto.setId(1L);
        commentOutputDto.setText("comment1");
        commentOutputDto.setAuthorName("user");
        commentOutputDto.setCreated(LocalDateTime.now());

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentOutputDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentOutputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentOutputDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentOutputDto.getAuthorName()), String.class));
    }
}