package ru.practicum.shareit.item.comment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Sql(scripts = {"classpath:commentRepositoryTest.sql"})
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Test
    void testFindAllByItem() {
        List<Comment> commentList = commentRepository.findAllByItem(Item.builder().id(1).build());

        assertNotNull(commentList);
        assertEquals(2, commentList.size());
    }

    @Test
    void testFindAllByItemIn() {
        List<Comment> commentList = commentRepository.findByItemIn(List.of(Item.builder().id(1).build(), Item.builder()
                .id(2).build()));

        assertNotNull(commentList);
        assertEquals(3, commentList.size());
    }

}
