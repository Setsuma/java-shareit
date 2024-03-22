package ru.practicum.shareit.item.comment.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
}
