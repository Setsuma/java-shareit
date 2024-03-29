package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingOutputDto> createBooking(@RequestBody BookingDto bookingDto,
                                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingOutputDto> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable long bookingId,
                                                           @RequestParam boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingOutputDto> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingOutputDto>> getAllUserBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(bookingService.getAllUserBooking(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingOutputDto>> getOwnerBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(bookingService.getOwnerBooking(userId, state, from, size));
    }
}
