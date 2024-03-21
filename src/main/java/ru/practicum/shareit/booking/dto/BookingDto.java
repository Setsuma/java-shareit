package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validator.CorrectDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@CorrectDate(startDate = "start", endDate = "end")
public class BookingDto {
    @NotNull(message = "id предмета не может быть пустым")
    private long itemId;
    @NotNull(message = "дата начала не может быть пустой")
    @FutureOrPresent(message = "дата начала не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "дата конца не может быть пустой")
    @Future(message = "дата конца не может быть в прошлом или настоящем")
    private LocalDateTime end;
}