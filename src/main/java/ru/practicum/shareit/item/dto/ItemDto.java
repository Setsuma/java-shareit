package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotEmpty(message = "имя не может быть пустым")
    private String name;
    @NotEmpty(message = "описание не может быть пустым")
    private String description;
    @NotNull(message = "поле доступности не может быть бустым")
    private Boolean available;
    private Long requestId;
}