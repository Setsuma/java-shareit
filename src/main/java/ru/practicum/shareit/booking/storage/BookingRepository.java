package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start DESC")
    List<Booking> findAllBookingsForBookerWithStartAndEnd(
            User user, LocalDateTime localDateTime);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.start < ?2 and b.end > ?2 " +
            "order by b.start DESC")
    List<Booking> findAllBookingsItemByForOwnerWithStartAndEnd(
            User user, LocalDateTime localDateTime);

    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(
            User booker, BookingStatus bookingStatus);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(User userId);

    @Query("select b from Booking b where b.item.owner = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime);


    List<Booking> findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
            User user, BookingStatus bookingStatus);

    @Query("select b from Booking b where b.item = ?1 and b.start > ?2 and b.status = 'APPROVED' order by b.start ASC")
    List<Booking> findNextBooking(Item item, LocalDateTime dateTime, Pageable limit);

    @Query("select b from Booking b where b.item = ?1 and b.start < ?2 and b.status = 'APPROVED' order by b.end Desc")
    List<Booking> findLastBooking(Item item, LocalDateTime dateTime, Pageable limit);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.booker = ?1 AND b.item = ?2 AND b.end < ?3")
    Boolean existBooking(User user, Item item, LocalDateTime dateTime);
}