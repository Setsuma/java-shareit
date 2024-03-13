package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {

    BookingOutputDto createBooking(BookingDto bookingDto, long userId);

    BookingOutputDto approveBooking(long userId, long bookingId, boolean isApprove);

    BookingOutputDto getBookingById(long userId, long bookingId);

    List<BookingOutputDto> getAllUserBooking(long userId, BookingState state);

    List<BookingOutputDto> getOwnerBooking(long userId, BookingState state);
}
