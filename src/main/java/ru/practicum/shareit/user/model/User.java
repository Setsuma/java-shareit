package ru.practicum.shareit.user.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    private String email;
}
