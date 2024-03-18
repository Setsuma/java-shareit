package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Sql(scripts = {"classpath:itemRepositoryTest.sql"})
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

    @Test
    void testFindAllByOwnerId() {
        List<Item> itemList = itemRepository.findByOwner_Id(1, pageable);

        assertNotNull(itemList);
        assertEquals(3, itemList.size());
    }

    @Test
    void testSearchItemsByText() {
        List<Item> itemList =
                itemRepository.search("hammer", pageable);
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testFindAllByRequestId() {
        List<Item> itemList =
                itemRepository.findByRequest_Id(1);
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testFindAllByRequestIn() {
        List<Item> itemList =
                itemRepository.findByRequestIn(List.of(ItemRequest.builder().id(1).build(), ItemRequest.builder()
                        .id(2).build()));
        assertNotNull(itemList);
        assertEquals(3, itemList.size());
    }
}
