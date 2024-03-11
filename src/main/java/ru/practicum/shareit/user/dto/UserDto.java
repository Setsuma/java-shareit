package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private long id;
    @NotEmpty(message = "имя не может быть пустым", groups = BasicInfo.class)
    private String name;
    @NotEmpty(message = "электронная почта не может быть пустой", groups = BasicInfo.class)
    @Email(message = "адрес электронный почты не соответствует формату Email", groups = {BasicInfo.class, AdvanceInfo.class})
    private String email;
}