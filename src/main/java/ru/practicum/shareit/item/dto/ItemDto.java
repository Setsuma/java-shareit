package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotEmpty(message = "имя не может быть пустым")
    @Size(max = 255, message = "слишком длинное имя")
    private String name;
    @NotEmpty(message = "описание не может быть пустым")
    @Size(max = 512, message = "слишком длинное описание")
    private String description;
    @NotNull(message = "поле доступности не может быть бустым")
    private Boolean available;
    private Long requestId;
}