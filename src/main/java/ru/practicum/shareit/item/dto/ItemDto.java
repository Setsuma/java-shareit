package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotEmpty(message = "имя не может быть пустым")
    private String name;
    @NotEmpty(message = "описание не может быть пустым")
    private String description;
    @NotNull(message = "поле доступности не может быть бустым")
    private Boolean available;
}
