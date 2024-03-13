package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ModelMapper mapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutputDto createBooking(BookingDto bookingDto, long userId) {
        Booking booking = mapper.map(bookingDto, Booking.class);

        booking.setItem(itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new IdNotFoundException("Предмет не найден")));

        if (booking.getItem().getOwner().getId() == userId)
            throw new IdNotFoundException("Нельзя бронировать свой предмет");

        if (!booking.getItem().getAvailable())
            throw new UnavailableException("Предмет пока недоступен для бронирования");

        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не найден")));

        booking.setStatus(BookingStatus.WAITING);
        return mapper.map(bookingRepository.save(booking), BookingOutputDto.class);
    }

    @Override
    public BookingOutputDto approveBooking(long userId, long bookingId, boolean isApprove) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронь не найдена"));

        if (booking.getStatus().equals(BookingStatus.APPROVED))
            throw new UnavailableException("бронь уже подтверждена");
        if (booking.getItem().getOwner().getId() == userId) {
            if (isApprove) booking.setStatus(BookingStatus.APPROVED);
            else booking.setStatus(BookingStatus.REJECTED);
        } else throw new IdNotFoundException("Нельзя подтвердить бронь, не являясь владельцем предмета");

        return mapper.map(bookingRepository.save(booking), BookingOutputDto.class);
    }

    @Override
    public BookingOutputDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронь не нвйдена"));

        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId)
            throw new IdNotFoundException("Бронь не найдена");

        return mapper.map(booking, BookingOutputDto.class);
    }

    @Override
    public List<BookingOutputDto> getAllUserBooking(long userId, BookingState state) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не нейден"));
        List<Booking> result = new ArrayList<>();

        switch (state) {
            case ALL: {
                result = bookingRepository.findAllByBookerOrderByStartDesc(booker);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findAllBookingsForBookerWithStartAndEnd(
                        booker, nowDateTime);
                break;
            }
            case PAST: {
                result = bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(
                        booker, nowDateTime);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(
                        booker, nowDateTime);
                break;
            }
            case WAITING: {
                result = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(
                        booker, BookingStatus.WAITING);
                break;
            }
            case REJECTED: {
                result = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(
                        booker, BookingStatus.REJECTED);
            }
        }
        return result.stream()
                .map(booking -> mapper.map(booking, BookingOutputDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getOwnerBooking(long userId, BookingState state) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не нейден"));
        List<Booking> result = new ArrayList<>();

        switch (state) {
            case ALL: {
                result = bookingRepository.findAllByItem_OwnerOrderByStartDesc(booker);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findAllBookingsItemByForOwnerWithStartAndEnd(booker, nowDateTime);
                break;
            }
            case PAST: {
                result = bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(booker, nowDateTime);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(booker, nowDateTime);
                break;
            }
            case WAITING: {
                result = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
                        booker, BookingStatus.WAITING);
                break;
            }
            case REJECTED: {
                result = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
                        booker, BookingStatus.REJECTED);
            }
        }
        return result.stream()
                .map(booking -> mapper.map(booking, BookingOutputDto.class))
                .collect(Collectors.toList());
    }
}