package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingForItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private BookingStatus status;
}