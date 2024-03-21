package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Sql(scripts = {"classpath:itemRequestRepositoryTest.sql"})
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

    @Test
    void testFindAllByRequesterId() {
        List<ItemRequest> itemList = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(1L);

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void testSearchItemsByText() {
        List<ItemRequest> itemList = itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(1L, pageable);

        assertNotNull(itemList);
        assertEquals(1, itemList.size());
    }

}