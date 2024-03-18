package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingControllerUnitTest {

    BookingService mockBookingService;
    BookingController bookingController;

    LocalDateTime start;
    LocalDateTime end;
    BookingDto bookingDto;
    BookingOutputDto bookingOutputDto;

    @BeforeAll
    void setUp() {
        start = LocalDateTime.now().minusDays(1);
        end = LocalDateTime.now().plusDays(1);
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

    @Test
    void createBooking() {
        when(mockBookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(bookingOutputDto);
        assertEquals(bookingOutputDto, bookingController.createBooking(bookingDto, 1L).getBody());
    }

    @Test
    void approveBooking() {
        BookingOutputDto approvedBooking = BookingOutputDto.builder().status(BookingStatus.APPROVED).build();

        when(mockBookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(approvedBooking);
        assertEquals(approvedBooking, bookingController.approveBooking(1L, 1L, true).getBody());
    }

    @Test
    void getBookingById() {
        when(mockBookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingOutputDto);
        assertEquals(bookingOutputDto, bookingController.getBookingById(1L, 1L).getBody());
    }

    @Test
    void getAllUserBooking() {
        when(mockBookingService.getAllUserBooking(anyLong(), any(), anyInt(), anyInt())).thenReturn(List.of(bookingOutputDto));
        assertEquals(List.of(bookingOutputDto), bookingController.getAllUserBooking(1L, BookingState.ALL, 0, 20).getBody());
    }

    @Test
    void getOwnerBooking() {
        when(mockBookingService.getOwnerBooking(anyLong(), any(), anyInt(), anyInt())).thenReturn(List.of(bookingOutputDto));
        assertEquals(List.of(bookingOutputDto), bookingController.getOwnerBooking(1L, BookingState.ALL, 0, 20).getBody());
    }
}
