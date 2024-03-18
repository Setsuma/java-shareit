package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService requestService;
    private final BookingService bookingService;
    private final ModelMapper mapper;
    private final CommentRepository commentRepository;
    private final EntityManager em;
    ItemRequest itemRequest1;
    UserDto ownerDto1;
    User owner1;
    UserDto requesterDto1;
    User requester1;
    UserDto bookerDto;
    User booker;
    UserDto userDtoForTest;
    User userForTest;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemDto itemDto1;
    ItemRequestOutputDto itemRequestDto1;
    Booking booking1;
    BookingDto bookingDto1;
    CommentOutputDto commentDto;

    TypedQuery<Item> query;
    @Autowired
    private ItemRepository itemRepositoryJpa;
    @Autowired
    private UserRepository userRepositoryJpa;
    @Autowired
    private BookingRepository bookingRepositoryJpa;
    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name ownerDto1")
                .email("ownerDto1@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .build();

        requesterDto1 = UserDto.builder()
                .name("name requesterDto101")
                .email("requesterDto101@mans.gf")
                .build();

        requester1 = User.builder()
                .id(requesterDto1.getId())
                .name(requesterDto1.getName())
                .email(requesterDto1.getEmail())
                .build();

        userDtoForTest = UserDto.builder()
                .name("name userDtoForTest")
                .email("userDtoForTest@userDtoForTest.zx")
                .build();

        userForTest = User.builder()
                .name(userDtoForTest.getName())
                .email(userDtoForTest.getEmail())
                .build();

        bookerDto = UserDto.builder()
                .name("booker")
                .email("booker@wa.dzd")
                .build();

        booker = User.builder()
                .name(bookerDto.getName())
                .email(bookerDto.getEmail())
                .build();

        itemRequest1 = ItemRequest.builder()
                .description("description for request 1")
                .requester(requester1)
                .created(now)
                .build();

        item1 = Item.builder()
                .name("name for item 1")
                .description("description for item 1")
                .owner(owner1)
                .available(true)
                .build();

        itemDto1 = ItemDto.builder()
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();

        itemRequestDto1 = ItemRequestOutputDto.builder()
                .description(item1.getDescription())
                .created(now)
                .build();

        commentDto = CommentOutputDto.builder()
                .id(1L)
                .created(now)
                .text("comment 1")
                .authorName(userForTest.getName())
                .build();

    }

    @Test
    void addItem_whenAllAreOk_returnSavedItemDto() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        query =
                em.createQuery("Select i from Item i", Item.class);
        List<Item> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());

        ItemDto savedItemDto = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        List<Item> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemDto.getId(), afterSave.get(0).getId());
        assertEquals(savedItemDto.getDescription(), afterSave.get(0).getDescription());
        assertEquals(savedItemDto.getName(), afterSave.get(0).getName());
    }

    @Test
    void addItem_whenUserNotFound_returnNotFoundRecordInDb() {
        assertThrows(IdNotFoundException.class, () -> itemService.createItem(itemDto1, 1000L));
    }

    @Test
    void getItemsByUserId_whenOk_returnItemDtoList() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDto = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getAllUserItems(savedOwnerDto1.getId(), 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDto.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDto.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDto.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDto.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDto.getAvailable(), itemDtos.get(0).getAvailable());
    }

    @Test
    void getItemsByUserId_whenUserNotFoundInBD_returnException() {
        assertThrows(IdNotFoundException.class, () -> itemService.getAllUserItems(1000L, 0, 20));
    }

    @Test
    void updateInStorage_whenAllIsOk_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getAllUserItems(savedOwnerDto1.getId(), 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updateItem = new ItemDto();
        updateItem.setName("new name");
        updateItem.setDescription("new description");

        ItemDto savedUpdItem =
                itemService.updateItem(updateItem, savedOwnerDto1.getId(), savedItemDtoBeforeUpd.getId());

        assertNotEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertNotEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void updateInStorage_whenAllFieldsItemIsNull_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getAllUserItems(savedOwnerDto1.getId(), 0, 20);

        assertEquals(1, itemDtos.size());
        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtos.get(0).getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtos.get(0).getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtos.get(0).getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtos.get(0).getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtos.get(0).getAvailable());

        ItemDto updateItem = new ItemDto();
        updateItem.setName(null);
        updateItem.setDescription(null);
        updateItem.setRequestId(null);
        updateItem.setAvailable(null);

        ItemDto savedUpdItem =
                itemService.updateItem(updateItem, savedOwnerDto1.getId(), savedItemDtoBeforeUpd.getId());

        assertEquals(savedItemDtoBeforeUpd.getName(), savedUpdItem.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), savedUpdItem.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getId(), savedUpdItem.getId());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), savedUpdItem.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), savedUpdItem.getAvailable());
    }

    @Test
    void updateInStorage_whenUpdatedItemHasOtherUser_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        UserDto savedOwnerDto2 = userService.createUser(userDtoForTest);
        ItemDto savedItemDtoBeforeUpd = itemService.createItem(itemDto1, savedOwnerDto2.getId());
        List<ItemWithBookingAndCommentsDto> itemDtos = itemService.getAllUserItems(savedOwnerDto1.getId(), 0, 20);

        assertEquals(0, itemDtos.size());

        ItemDto updateItem = savedItemDtoBeforeUpd;
        updateItem.setName("new name");
        updateItem.setDescription("new description");
        assertThrows(IdNotFoundException.class,
                () -> itemService.updateItem(updateItem,
                        savedOwnerDto1.getId(), savedItemDtoBeforeUpd.getId()));
    }

    @Test
    void updateInStorage_whenItemNotFoundInDb_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        Long itemId = 1001L;
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> itemService.updateItem(itemDto1, savedOwnerDto1.getId(), itemId));
        assertEquals("Предмет не найден", ex.getMessage());
    }

    @Test
    void getItemById_whenOk_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDtoBeforeUpd = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        ItemWithBookingAndCommentsDto itemDtoFromBd = itemService.getItemById(savedOwnerDto1.getId(), savedItemDtoBeforeUpd.getId());

        assertEquals(savedItemDtoBeforeUpd.getId(), itemDtoFromBd.getId());
        assertEquals(savedItemDtoBeforeUpd.getName(), itemDtoFromBd.getName());
        assertEquals(savedItemDtoBeforeUpd.getDescription(), itemDtoFromBd.getDescription());
        assertEquals(savedItemDtoBeforeUpd.getRequestId(), itemDtoFromBd.getRequestId());
        assertEquals(savedItemDtoBeforeUpd.getAvailable(), itemDtoFromBd.getAvailable());
    }

    @Test
    void getItemById_whenWrongUser_returnItemFromDb() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDto = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        assertThrows(IdNotFoundException.class, () -> itemService.getItemById(savedOwnerDto1.getId() + 100, savedItemDto.getId()));
    }

    @Test
    void testSearchItemsByText() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDto01 = itemService.createItem(itemDto1, savedOwnerDto1.getId());

        UserDto savedRequester = userService.createUser(requesterDto1);
        ItemDto itemDto02 = new ItemDto();
        itemDto02.setName("new item");
        itemDto02.setDescription("new description");
        itemDto02.setAvailable(true);
        ItemDto savedItemDto02 = itemService.createItem(itemDto02, savedOwnerDto1.getId());

        List<ItemDto> itemDtoList = itemService.getSearchItems("nEw", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(itemDto02.getDescription(), itemDtoList.stream().findFirst().get().getDescription());
    }

    @Test
    void searchItemsByText_whenTextIsBlank() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDto01 = itemService.createItem(itemDto1, savedOwnerDto1.getId());

        UserDto savedRequester = userService.createUser(requesterDto1);
        ItemDto itemDto02 = new ItemDto();
        itemDto02.setName("new item");
        itemDto02.setDescription("new description");
        itemDto02.setAvailable(true);

        ItemDto savedItemDto02 = itemService.createItem(itemDto02, savedOwnerDto1.getId());

        List<ItemDto> itemDtoList = itemService.getSearchItems("", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(0, itemDtoList.size());
    }

    @Test
    void getItemWithBookingAndComment() {
        UserDto savedBooker = userService.createUser(bookerDto);
        booker.setId(savedBooker.getId());
        bookerDto.setId(savedBooker.getId());
        UserDto bookerForResponse = mapper.map(booker, UserDto.class);
        assertEquals(savedBooker.getId(), booker.getId());
        assertEquals(savedBooker.getName(), booker.getName());
        assertEquals(savedBooker.getEmail(), booker.getEmail());


        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        owner1.setId(savedOwnerDto1.getId());
        ownerDto1.setId(savedOwnerDto1.getId());
        assertEquals(savedOwnerDto1.getId(), owner1.getId());
        assertEquals(savedOwnerDto1.getName(), owner1.getName());
        assertEquals(savedOwnerDto1.getEmail(), owner1.getEmail());

        ItemDto savedItemDto01 = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        itemDto1.setId(savedItemDto01.getId());
        item1.setId(savedItemDto01.getId());
        assertEquals(savedItemDto01.getId(), item1.getId());
        assertEquals(savedItemDto01.getName(), item1.getName());
        assertEquals(savedItemDto01.getDescription(), item1.getDescription());

        bookingDto1 = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusSeconds(1)).end(now.plusSeconds(2))
                .build();

        booking1 = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(bookingDto1.getStart()).end(bookingDto1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();

        BookingOutputDto savedBookingForResponse = bookingService.createBooking(bookingDto1, bookerDto.getId());
        booking1.setId(savedBookingForResponse.getId());
        booking1.setItem(item1);
        assertDoesNotThrow(() -> Thread.sleep(1500));

        Comment comment1 = Comment.builder().text("content commentary").item(item1).author(booker)
                .createdDate(LocalDateTime.now()).build();
        Comment savedComment1 = commentRepository.save(comment1);
        comment1.setId(savedComment1.getId());

        ItemWithBookingAndCommentsDto result =
                itemService.getItemById(owner1.getId(), item1.getId());

        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
        assertEquals(comment1.getText(), result.getComments().get(0).getText());
        assertEquals(comment1.getAuthor().getName(), result.getComments().get(0).getAuthorName());
    }

    @Test
    void getItemWithBookingAndComment_whenAllIsOk_returnItemWithBookingAndCommentsDto() {
        UserDto savedBooker = userService.createUser(bookerDto);
        booker.setId(savedBooker.getId());
        bookerDto.setId(savedBooker.getId());
        UserDto bookerForResponse = mapper.map(booker, UserDto.class);


        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        owner1.setId(savedBooker.getId());
        ownerDto1.setId(savedBooker.getId());

        ItemDto savedItemDto01 = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        itemDto1.setId(savedItemDto01.getId());
        item1.setId(savedItemDto01.getId());

        bookingDto1 = BookingDto.builder()
                .itemId(item1.getId())
                .start(now.plusSeconds(1)).end(now.plusSeconds(2))
                .build();

        BookingOutputDto savedBookingForResponse = bookingService.createBooking(bookingDto1, bookerDto.getId());

        booking1 = Booking.builder().id(savedBookingForResponse.getId())
                .item(item1)
                .booker(booker)
                .start(bookingDto1.getStart())
                .end(bookingDto1.getEnd())
                .status(BookingStatus.APPROVED)
                .build();
        assertDoesNotThrow(() -> Thread.sleep(1500));   //Чтобы бронь стала прошедшей.

        Comment comment1 = Comment.builder().text("commentary").item(item1).author(booker)
                .createdDate(LocalDateTime.now()).build();
        Comment savedComment1 = commentRepository.save(comment1);

        ItemWithBookingAndCommentsDto result =
                itemService.getItemById(owner1.getId(), item1.getId());

        assertEquals(item1.getName(), result.getName());
        assertEquals(item1.getDescription(), result.getDescription());
        assertEquals(item1.getAvailable(), result.getAvailable());
    }

    @Test
    void getItemWithBookingAndComment_whenOwnerNotFound_returnNotFoundRecordInBD() {
        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        ItemDto savedItemDto01 = itemService.createItem(itemDto1, savedOwnerDto1.getId());
        Long ownerId = 1001L;

        IdNotFoundException ex = assertThrows(IdNotFoundException.class, () ->
                itemService.getItemById(ownerId, savedItemDto01.getId()));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void saveComment_thenReturnExceptions() {
        UserDto savedUser1 = userService.createUser(ownerDto1);
        UserDto savedUser2 = userService.createUser(userDtoForTest);
        ItemDto savedItem = itemService.createItem(itemDto1, savedUser1.getId());
        CommentDto commentDto = new CommentDto();

        bookingDto1 = new BookingDto();
        bookingDto1.setItemId(savedItem.getId());

        bookingDto1.setStart(now.plusHours(1));
        bookingDto1.setEnd(now.plusHours(10));
        bookingService.createBooking(bookingDto1, savedUser2.getId());

        UnavailableException ex = assertThrows(UnavailableException.class,
                () -> itemService.createComment(commentDto, savedUser1.getId(), savedItem.getId()));
        assertEquals("Отзывы могут оставлять только те люди, которые уже пользовались вещью",
                ex.getMessage());
    }

    @Test
    void saveComment_whenItemNotFound_thenReturnNotFoundRecordInDb() {
        UserDto savedUser1 = userService.createUser(ownerDto1);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        Long notFoundItemId = 1001L;

        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> itemService.createComment(commentDto, savedUser1.getId(), notFoundItemId));
        assertEquals("Предмет не найден", ex.getMessage());
    }

    @Test
    void saveComment_whenAllAreOk_thenReturnComment() {
        CommentDto inputCommentDto = CommentDto.builder().text("new comment for test").build();

        User owner2 = User.builder()
                .id(2L)
                .name("name for owner")
                .email("owner2@aadmf.wreew")
                .build();

        User userForTest2 = User.builder()
                .id(1L)
                .name("name user for test 2")
                .email("userForTest2@ahd.ew")
                .build();

        Item zaglushka = Item.builder().id(1L).name("zaglushka").description("desc zaglushka")
                .owner(owner2).build();

        Booking bookingFromBd = Booking.builder()
                .id(1L)
                .item(zaglushka)
                .booker(userForTest2)
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .build();

        Item itemFromBd = Item.builder()
                .id(1L)
                .name("name for item")
                .description("desc for item")
                .owner(owner2)
                .available(true)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .text("comment 1")
                .build();

        Comment outputComment = Comment.builder()
                .id(1L)
                .author(userForTest2)
                .text("comment 1")
                .item(itemFromBd)
                .build();

        UserRepository userRepositoryJpa2 = mock(UserRepository.class);
        ItemRepository itemRepositoryJpa2 = mock(ItemRepository.class);
        CommentRepository commentRepository2 = mock(CommentRepository.class);
        BookingRepository bookingRepository2 = mock(BookingRepository.class);
        ItemService itemService2 = new ItemServiceImpl(mapper, itemRepositoryJpa2, userRepositoryJpa2, bookingRepository2, commentRepository2, itemRequestRepository);

        when(userRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(userForTest2));
        when(itemRepositoryJpa2.findById(any()))
                .thenReturn(Optional.of(itemFromBd));
        when(commentRepository2.save(any()))
                .thenReturn(outputComment);
        when(bookingRepository2.existBooking(any(), any(), any())).thenReturn(true);

        CommentOutputDto outputCommentDto =
                itemService2.createComment(inputCommentDto, userForTest2.getId(), itemFromBd.getId());

        assertEquals(commentDto.getText(), outputCommentDto.getText());
        assertEquals(userForTest2.getName(), outputCommentDto.getAuthorName());
        assertEquals(1L, outputCommentDto.getId());
        assertNotEquals(LocalDateTime.now(), outputCommentDto.getCreated());
    }


    @Test
    void saveComment_whenUserNotFound_thenReturnNotFoundRecordInBD() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        assertThrows(IdNotFoundException.class, () -> itemService.createComment(commentDto, 1000L, 1L));
    }
}