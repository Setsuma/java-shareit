package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingControllerIntegrationTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    BookingService mockBookingService;
    BookingController bookingController;

    LocalDateTime start;
    LocalDateTime end;
    BookingDto bookingDto;
    BookingOutputDto bookingOutputDto;

    @BeforeAll
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end).build();
        bookingOutputDto = BookingOutputDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(1L).build())
                .build();

        mockBookingService = Mockito.mock(BookingService.class);
        bookingController = new BookingController(mockBookingService);
    }

    @SneakyThrows
    @Test
    void add_whenAllIsOk_returnBookingForResponse() {
        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(bookingOutputDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutputDto), result);
    }

//    @SneakyThrows
//    @Test
//    void add_whenEndTimeBeforeStartTime_returnValidateException() {
//        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
//                .id(1L)
//                .startTime(now.plusDays(2))
//                .endTime(now.plusDays(1))
//                .item(itemForResponseDtoMapper.mapToDto(item1))
//                .booker(userOnlyWithIdDtoMapper.mapToDto(booker101))
//                .status(BookingStatus.WAITING).build();
//
//        bookingDtoForCreate = BookingDto.builder()
//                .id(1L)
//                .itemId(1L)
//                .booker(UserForResponseDto.builder().id(booker101.getId()).name(booker101.getName()).build())
//                .startTime(bookingDto1ForResponse.getStartTime())
//                .endTime(bookingDto1ForResponse.getEndTime())
//                .bookingStatus(BookingStatus.WAITING)
//                .build();
//
//        Booking booking = Booking.builder()
//                .id(bookingDtoForCreate.getId())
//                .item(item1)
//                .booker(booker101)
//                .startTime(bookingDtoForCreate.getStartTime())
//                .endTime(bookingDtoForCreate.getEndTime())
//                .bookingStatus(bookingDtoForCreate.getBookingStatus())
//                .build();
//
//        item1.setBookings(List.of(booking));
////        when(itemRepositoryJpa.findById(any())).thenReturn(Optional.of(item1));
////        when(userRepositoryJpa.findById(any())).thenReturn(Optional.of(booker101));
//        when(bookingService.createBooking(any(), any())).thenThrow(ValidateException.class);
//
//        String result = mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", booker101.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(bookingDtoForCreate)))
//
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        assertEquals("Error messagenull", result);
//    }

    @SneakyThrows
    @Test
    void updateByOwner() {
        BookingOutputDto approvedBooking = BookingOutputDto.builder().status(BookingStatus.APPROVED).build();


        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedBooking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(approvedBooking), result);
    }

    @SneakyThrows
    @Test
    void getWithStatusById() {

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingOutputDto);
        String result = mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutputDto), result);
    }

    @SneakyThrows
    @Test
    void getByUserId() {
        when(bookingService.getAllUserBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingOutputDto)), result);
    }

    @SneakyThrows
    @Test
    void getByOwnerId() {
        when(bookingService.getOwnerBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingOutputDto)), result);
    }
}
