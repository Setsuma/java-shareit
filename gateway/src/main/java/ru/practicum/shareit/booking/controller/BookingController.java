package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingService;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable long bookingId,
                                                           @RequestParam boolean approved) {
        return bookingService.approveBooking(userId, bookingId, String.valueOf(approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return bookingService.getAllUserBooking(userId,  from, size, state.name());
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return bookingService.getOwnerBooking(userId,  from, size, state.name());
    }
}
