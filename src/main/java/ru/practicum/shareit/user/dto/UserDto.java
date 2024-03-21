package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private long id;
    @NotEmpty(message = "имя не может быть пустым", groups = BasicInfo.class)
    @Size(max = 255, message = "слишком длинное имя")
    private String name;
    @NotEmpty(message = "электронная почта не может быть пустой", groups = BasicInfo.class)
    @Email(message = "адрес электронный почты не соответствует формату Email", groups = {BasicInfo.class, AdvanceInfo.class})
    @Size(max = 512, message = "слишком длинная почта")
    private String email;
}