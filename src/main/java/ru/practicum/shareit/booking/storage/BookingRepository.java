package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User booker, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllBookingsForBookerWithStartAndEnd(
            User user, LocalDateTime localDateTime, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllBookingsItemByForOwnerWithStartAndEnd(
            User user, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByBookerAndEndIsBefore(
            User user, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByBookerAndStartIsAfter(
            User user, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(
            User booker, BookingStatus bookingStatus, Sort sort);

    List<Booking> findAllByItem_Owner(User userId, Sort sort);

    @Query("select b from Booking b where b.item.owner = ?1 and b.end < ?2")
    List<Booking> findAllByItem_OwnerAndEndIsBefore(
            User user, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByItem_OwnerAndStartIsAfter(
            User user, LocalDateTime localDateTime, Sort sort);


    List<Booking> findAllByItem_OwnerAndStatusEquals(
            User user, BookingStatus bookingStatus, Sort sort);

    @Query("select b from Booking b where b.item = ?1 and b.start > ?2 and b.status = 'APPROVED' order by b.start ASC")
    List<Booking> findNextBooking(Item item, LocalDateTime dateTime, Pageable limit);

    @Query("select b from Booking b where b.item = ?1 and b.start < ?2 and b.status = 'APPROVED' order by b.end Desc")
    List<Booking> findLastBooking(Item item, LocalDateTime dateTime, Pageable limit);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.booker = ?1 AND b.item = ?2 AND b.end < ?3")
    Boolean existBooking(User user, Item item, LocalDateTime dateTime);
}