package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    @NotEmpty(message = "Комментарий не может быть пустым")
    private String text;
}
