package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Sql(scripts = {"classpath:bookingRepositoryTest.sql"})
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());

    @Test
    void findAllByBookerTest() {
        List<Booking> bookingList = bookingRepository.findAllByBooker(User.builder().id(1).build(), pageable);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllBookingsForBookerWithStartAndEndTest() {
        List<Booking> bookingList = bookingRepository.findAllBookingsForBookerWithStartAndEnd(User.builder().id(2).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllBookingsItemByForOwnerWithStartAndEndTest() {
        List<Booking> bookingList = bookingRepository.findAllBookingsItemByForOwnerWithStartAndEnd(User.builder().id(1).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByBookerAndEndIsBeforeTest() {
        List<Booking> bookingList = bookingRepository.findAllBookingsItemByForOwnerWithStartAndEnd(User.builder().id(2).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(0, bookingList.size());
    }

    @Test
    void findAllByBookerAndStartIsAfterTest() {
        List<Booking> bookingList = bookingRepository.findAllByBookerAndStartIsAfter(User.builder().id(2).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(2, bookingList.size());
    }

    @Test
    void findAllByBookerAndStatusEqualsTest() {
        List<Booking> bookingList = bookingRepository.findAllByBookerAndStatusEquals(User.builder().id(2).build(),
                BookingStatus.APPROVED, pageable);

        assertNotNull(bookingList);
        assertEquals(3, bookingList.size());
    }

    @Test
    void findAllByItem_OwnerTest() {
        List<Booking> bookingList = bookingRepository.findAllByItem_Owner(User.builder().id(1).build(), pageable);

        assertNotNull(bookingList);
        assertEquals(4, bookingList.size());
    }

    @Test
    void findAllByItem_OwnerAndEndIsBeforeTest() {
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerAndEndIsBefore(User.builder().id(1).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void findAllByItem_OwnerAndStartIsAfterTest() {
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerAndStartIsAfter(User.builder().id(1).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(2, bookingList.size());
    }

    @Test
    void findAllByItem_OwnerAndStatusEqualsTest() {
        List<Booking> bookingList = bookingRepository.findAllByItem_OwnerAndStatusEquals(User.builder().id(1).build(),
                BookingStatus.WAITING, pageable);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void findNextBookingTest() {
        List<Booking> bookingList = bookingRepository.findNextBooking(Item.builder().id(1).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 5);
    }

    @Test
    void findLastBookingTest() {
        List<Booking> bookingList = bookingRepository.findLastBooking(Item.builder().id(1).build(),
                LocalDateTime.now(), pageable);

        assertNotNull(bookingList);
        assertEquals(2, bookingList.size());
        assertEquals(3, (bookingList.get(0).getId()));
    }

    @Test
    void existBooking() {
        Assertions.assertTrue(bookingRepository.existBooking(User.builder().id(2).build(), Item.builder().id(1).build(), LocalDateTime.now()));
    }
}
