package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final ModelMapper mapper;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingService bookingService;
    private BookingRepository bookingRepositoryJpa;

    User user;
    UserDto userForResponse;
    User owner;
    UserDto ownerForResponseDto;
    Item item;
    BookingDto bookingDto;
    Booking booking;
    BookingDto bookingDto777;
    Booking booking777;
    //Current
    BookingDto currentBookingDto;
    Booking currentBooking;
    //Past
    BookingDto pastBookingDto;
    Booking pastBooking;
    //Future
    BookingDto futureBookingDto;
    Booking futureBooking;
    //Waiting
    BookingDto waitingBookingDto;
    Booking waitingBooking;
    //Rejected
    BookingDto rejectedBookingDto;
    Booking rejectedBooking;

    @BeforeEach
    void setUp() {
        bookingRepositoryJpa = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingService = new BookingServiceImpl(mapper, bookingRepositoryJpa, userRepository, itemRepository);

        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .name("name user 1")
                .email("user1@ugvg@rsdx")
                .build();

        userForResponse = mapper.map(user, UserDto.class);

        owner = User.builder()
                .id(2L)
                .name("name owner 2")
                .email("owner@jjgv.zw")
                .build();

        ownerForResponseDto = mapper.map(owner, UserDto.class);

        item = Item.builder()
                .id(1L)
                .name("name item 1")
                .description("desc item 1")
                .owner(owner)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        bookingDto777 = BookingDto.builder()
                .itemId(item.getId())
                .start(now.plusHours(36))
                .end(now.plusHours(60))
                .build();

        booking777 = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(bookingDto777.getStart())
                .end(bookingDto777.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        //Current
        currentBookingDto = bookingDto.toBuilder()
                .itemId(item.getId())
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .build();

        currentBooking = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .start(currentBookingDto.getStart())
                .end(currentBookingDto.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        //Past
        pastBookingDto = bookingDto.toBuilder()
                .itemId(item.getId())
                .start(now.minusDays(1000))
                .end(now.minusDays(999))
                .build();

        pastBooking = Booking.builder()
                .id(3L)
                .item(item)
                .booker(user)
                .start(pastBookingDto.getStart())
                .end(pastBookingDto.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        //Future
        futureBookingDto = bookingDto.toBuilder()
                .start(now.minusDays(999))
                .end(now.minusDays(1000))
                .build();

        futureBooking = Booking.builder()
                .id(4L)
                .item(item)
                .booker(user)
                .start(futureBookingDto.getStart())
                .end(futureBookingDto.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        //Waiting
        waitingBookingDto = bookingDto.toBuilder()
                .start(now.plusDays(1))
                .end(now.minusDays(2))
                .build();

        waitingBooking = Booking.builder()
                .id(5L)
                .item(item)
                .booker(user)
                .start(waitingBookingDto.getStart())
                .end(waitingBookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        //Rejected
        rejectedBookingDto = bookingDto.toBuilder()
                .start(now.plusDays(100))
                .end(now.plusDays(101))
                .build();

        rejectedBooking = Booking.builder()
                .id(6L)
                .item(item)
                .booker(user)
                .start(rejectedBookingDto.getStart())
                .end(rejectedBookingDto.getEnd())
                .status(BookingStatus.REJECTED)
                .build();
    }

    @Test
    void createBooking_whenAllAreOk_returnSavedBookingDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepositoryJpa.save(any())).thenReturn(booking);
        BookingOutputDto savedBookingForResponse = bookingService.createBooking(bookingDto, user.getId());

        assertNotNull(savedBookingForResponse);
        assertEquals(bookingDto.getStart(), savedBookingForResponse.getStart());
        assertEquals(bookingDto.getEnd(), savedBookingForResponse.getEnd());
        assertEquals(bookingDto.getItemId(), savedBookingForResponse.getItem().getId());
    }

    @Test
    void createBooking_whenItemNotFoundInDb_returnIdNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, user.getId()));
        assertEquals(String.format("Предмет не найден",
                bookingDto.getItemId()), ex.getMessage());
    }

    @Test
    void createBooking_whenItemAvailableIsFalse_returnValidateException() {
        item.setAvailable(false);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        UnavailableException ex = assertThrows(UnavailableException.class,
                () -> bookingService.createBooking(bookingDto, 1L));
        assertEquals("Предмет пока недоступен для бронирования", ex.getMessage());
    }

    @Test
    void updateBooking_whenAllIsOk_returnUpdateBooking() {
        Booking updatedBooking = booking.toBuilder().status(BookingStatus.APPROVED).build();

        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.save(any())).thenReturn(updatedBooking);

        BookingOutputDto updatedBookingForResponse = bookingService.approveBooking(owner.getId(),
                booking.getId(), true);

        assertNotNull(updatedBookingForResponse);
        assertEquals(bookingDto.getStart(), updatedBookingForResponse.getStart());
        assertEquals(bookingDto.getEnd(), updatedBookingForResponse.getEnd());
        assertEquals(bookingDto.getItemId(), updatedBookingForResponse.getItem().getId());
    }

    @Test
    void updateBooking_whenUserNotFound_returnUpdateBooking() {
        Booking updatedBooking = booking.toBuilder().status(BookingStatus.APPROVED).build();

        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());
        when(bookingRepositoryJpa.save(any())).thenReturn(updatedBooking);

        assertThrows(IdNotFoundException.class, () -> bookingService.approveBooking(owner.getId(),
                booking.getId(), true));
    }

    @Test
    void updateBooking_whenUserIsNotOwnerForItem_returnIdNotFoundExceptionException() {
        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        IdNotFoundException ex =
                assertThrows(IdNotFoundException.class, () -> bookingService.approveBooking(user.getId(),
                        booking.getId(), true));
        assertEquals("Нельзя подтвердить бронь, не являясь владельцем предмета", ex.getMessage());
    }

    @Test
    void updateBooking_whenBookingIsApproved_returnValidateException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepositoryJpa.findById(any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        UnavailableException ex =
                assertThrows(UnavailableException.class,
                        () -> bookingService.approveBooking(1L, user.getId(), true));
        assertEquals("бронь уже подтверждена", ex.getMessage());
    }

    @Test
    void getWithStatusById_whenRequestByOwnerOrBooker_returnBookingForResponse() {
        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingOutputDto outputBooking = bookingService.getBookingById(owner.getId(), booking.getId());

        assertEquals(booking.getId(), outputBooking.getId());
        assertEquals(booking.getBooker().getId(), outputBooking.getBooker().getId());
        assertEquals(booking.getItem().getName(), outputBooking.getItem().getName());
        assertEquals(booking.getStart(), outputBooking.getStart());
        assertEquals(booking.getEnd(), outputBooking.getEnd());
        assertEquals(booking.getStatus(), outputBooking.getStatus());
    }

    @Test
    void getWithStatusById_whenRequestByWrongUser_returnBookingForResponse() {
        when(bookingRepositoryJpa.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(IdNotFoundException.class, () -> bookingService.getBookingById(1000L, booking.getId()));
    }

    @Test
    void getByUserId_whenStateIsAll_returnAllBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBooker(any(), any()))
                .thenReturn(List.of(booking));
        List<BookingOutputDto> result = bookingService.getAllUserBooking(user.getId(), BookingState.ALL, 0, 5);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(booking.getStart(), result.get(0).getStart());
        assertEquals(booking.getEnd(), result.get(0).getEnd());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsCurrent_returnAllCurrentBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllBookingsForBookerWithStartAndEnd(any(), any(), any()))
                .thenReturn(List.of(currentBooking));
        List<BookingOutputDto> result = bookingService.getAllUserBooking(user.getId(), BookingState.CURRENT, 0, 5);

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertEquals(currentBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(currentBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(currentBooking.getStart(), result.get(0).getStart());
        assertEquals(currentBooking.getEnd(), result.get(0).getEnd());
        assertEquals(currentBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsPast_returnAllPastBookings() {

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndEndIsBefore(any(), any(), any()))
                .thenReturn(List.of(pastBooking));
        List<BookingOutputDto> result = bookingService.getAllUserBooking(user.getId(), BookingState.PAST, 0, 5);

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
        assertEquals(pastBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(pastBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(pastBooking.getStart(), result.get(0).getStart());
        assertEquals(pastBooking.getEnd(), result.get(0).getEnd());
        assertEquals(pastBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsFuture_returnAllFutureBookings() {

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndStartIsAfter(any(), any(), any()))
                .thenReturn(List.of(futureBooking));
        List<BookingOutputDto> result = bookingService.getAllUserBooking(user.getId(), BookingState.FUTURE, 0, 5);

        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
        assertEquals(futureBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(futureBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(futureBooking.getStart(), result.get(0).getStart());
        assertEquals(futureBooking.getEnd(), result.get(0).getEnd());
        assertEquals(futureBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsWaiting_returnAllWaitingBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndStatusEquals(any(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        List<BookingOutputDto> result = bookingService.getAllUserBooking(user.getId(), BookingState.WAITING, 0, 5);

        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
        assertEquals(waitingBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(waitingBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(waitingBooking.getStart(), result.get(0).getStart());
        assertEquals(waitingBooking.getEnd(), result.get(0).getEnd());
        assertEquals(waitingBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByUserId_whenStateIsRejected_returnAllRejectedBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepositoryJpa.findAllByBookerAndStatusEquals(any(), any(), any()))
                .thenReturn(List.of(rejectedBooking));
        List<BookingOutputDto> result = bookingService.getAllUserBooking(user.getId(), BookingState.REJECTED, 0, 5);

        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
        assertEquals(rejectedBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(rejectedBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(rejectedBooking.getStart(), result.get(0).getStart());
        assertEquals(rejectedBooking.getEnd(), result.get(0).getEnd());
        assertEquals(rejectedBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId_whenUserNotFoundInDb_returnIdNotFoundException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> bookingService.getOwnerBooking(1L, BookingState.ALL, 0, 3));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void getByOwnerId_whenStateIsAll_returnAllBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.findAllByItem_Owner(any(), any()))
                .thenReturn(List.of(booking));
        List<BookingOutputDto> result = bookingService.getOwnerBooking(owner.getId(), BookingState.ALL, 0, 5);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(booking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(booking.getStart(), result.get(0).getStart());
        assertEquals(booking.getEnd(), result.get(0).getEnd());
        assertEquals(booking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId_whenStateIsCurrent_returnAllCurrentBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.findAllBookingsItemByForOwnerWithStartAndEnd(any(), any(), any()))
                .thenReturn(List.of(currentBooking));
        List<BookingOutputDto> result =
                bookingService.getOwnerBooking(user.getId(), BookingState.CURRENT, 0, 5);

        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
        assertEquals(currentBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(currentBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(currentBooking.getStart(), result.get(0).getStart());
        assertEquals(currentBooking.getEnd(), result.get(0).getEnd());
        assertEquals(currentBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId_whenStateIsPast_returnAllPastBookings() {

        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.findAllByItem_OwnerAndEndIsBefore(any(), any(), any()))
                .thenReturn(List.of(pastBooking));
        List<BookingOutputDto> result = bookingService.getOwnerBooking(owner.getId(), BookingState.PAST, 0, 5);

        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
        assertEquals(pastBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(pastBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(pastBooking.getStart(), result.get(0).getStart());
        assertEquals(pastBooking.getEnd(), result.get(0).getEnd());
        assertEquals(pastBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId_whenStateIsFuture_returnAllFutureBookings() {

        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.findAllByItem_OwnerAndStartIsAfter(any(), any(), any()))
                .thenReturn(List.of(futureBooking));
        List<BookingOutputDto> result = bookingService.getOwnerBooking(owner.getId(), BookingState.FUTURE, 0, 5);

        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
        assertEquals(futureBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(futureBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(futureBooking.getStart(), result.get(0).getStart());
        assertEquals(futureBooking.getEnd(), result.get(0).getEnd());
        assertEquals(futureBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId_whenStateIsWaiting_returnAllWaitingBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.findAllByItem_OwnerAndStatusEquals(any(), any(), any()))
                .thenReturn(List.of(waitingBooking));
        List<BookingOutputDto> result = bookingService.getOwnerBooking(owner.getId(), BookingState.WAITING, 0, 5);

        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
        assertEquals(waitingBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(waitingBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(waitingBooking.getStart(), result.get(0).getStart());
        assertEquals(waitingBooking.getEnd(), result.get(0).getEnd());
        assertEquals(waitingBooking.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getByOwnerId_whenStateIsRejected_returnAllRejectedBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepositoryJpa.findAllByItem_OwnerAndStatusEquals(any(), any(), any()))
                .thenReturn(List.of(rejectedBooking));
        List<BookingOutputDto> result = bookingService.getOwnerBooking(owner.getId(), BookingState.REJECTED, 0, 5);

        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
        assertEquals(rejectedBooking.getBooker().getId(), result.get(0).getBooker().getId());
        assertEquals(rejectedBooking.getItem().getName(), result.get(0).getItem().getName());
        assertEquals(rejectedBooking.getStart(), result.get(0).getStart());
        assertEquals(rejectedBooking.getEnd(), result.get(0).getEnd());
        assertEquals(rejectedBooking.getStatus(), result.get(0).getStatus());
    }
}