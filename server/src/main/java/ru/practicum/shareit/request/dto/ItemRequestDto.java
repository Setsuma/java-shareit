package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    @NotEmpty(message = "Описание не может быть пустым")
    @Size(max = 512, message = "слишком длинное описание")
    private String description;
}