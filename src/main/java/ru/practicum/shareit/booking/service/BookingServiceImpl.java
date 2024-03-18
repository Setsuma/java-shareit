package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("Пользователь не найден"));
        if (booking.getStatus().equals(BookingStatus.APPROVED))
            throw new UnavailableException("бронь уже подтверждена");
        if (booking.getItem().getOwner().getId() == user.getId()) {
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
    public List<BookingOutputDto> getAllUserBooking(long userId, BookingState state, int from, int size) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не нейден"));
        List<Booking> result = new ArrayList<>();

        switch (state) {
            case ALL: {
                result = bookingRepository.findAllByBooker(booker, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findAllBookingsForBookerWithStartAndEnd(
                        booker, nowDateTime, pageable);
                break;
            }
            case PAST: {
                result = bookingRepository.findAllByBookerAndEndIsBefore(
                        booker, nowDateTime, pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findAllByBookerAndStartIsAfter(
                        booker, nowDateTime, pageable);
                break;
            }
            case WAITING: {
                result = bookingRepository.findAllByBookerAndStatusEquals(
                        booker, BookingStatus.WAITING, pageable);
                break;
            }
            case REJECTED: {
                result = bookingRepository.findAllByBookerAndStatusEquals(
                        booker, BookingStatus.REJECTED, pageable);
            }
        }
        return result.stream()
                .map(booking -> mapper.map(booking, BookingOutputDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getOwnerBooking(long userId, BookingState state, int from, int size) {
        final LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь не найден"));
        List<Booking> result = new ArrayList<>();

        switch (state) {
            case ALL: {
                result = bookingRepository.findAllByItem_Owner(booker, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findAllBookingsItemByForOwnerWithStartAndEnd(booker,
                        nowDateTime, pageable);
                break;
            }
            case PAST: {
                result = bookingRepository.findAllByItem_OwnerAndEndIsBefore(booker,
                        nowDateTime, pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findAllByItem_OwnerAndStartIsAfter(booker,
                        nowDateTime, pageable);
                break;
            }
            case WAITING: {
                result = bookingRepository.findAllByItem_OwnerAndStatusEquals(
                        booker, BookingStatus.WAITING, pageable);
                break;
            }
            case REJECTED: {
                result = bookingRepository.findAllByItem_OwnerAndStatusEquals(
                        booker, BookingStatus.REJECTED, pageable);
            }
        }
        return result.stream()
                .map(booking -> mapper.map(booking, BookingOutputDto.class))
                .collect(Collectors.toList());
    }
}