package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager em;


    ItemRequest itemRequest1;
    UserDto ownerDto1;
    UserDto requesterDto1;
    User owner1;
    User requester1;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemRequestDto itemRequestDto1;

    TypedQuery<ItemRequest> query;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        ownerDto1 = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();
        requesterDto1 = UserDto.builder()
                .name("name userDto2")
                .email("userDto2@mans.gf")
                .build();

        owner1 = User.builder()
                .id(ownerDto1.getId())
                .name(ownerDto1.getName())
                .email(ownerDto1.getEmail())
                .build();

        requester1 = User.builder()
                .id(requesterDto1.getId())
                .name(requesterDto1.getName())
                .email(requesterDto1.getEmail())
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

        itemRequestDto1 = ItemRequestDto.builder()
                .description(item1.getDescription())
                .build();
    }

    @Test
    void addItemRequest() {

        UserDto savedOwnerDto1 = userService.createUser(ownerDto1);
        query =
                em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> beforeSave = query.getResultList();

        assertEquals(0, beforeSave.size());


        ItemRequestOutputDto savedItemRequest =
                itemRequestService.addItemRequest(itemRequestDto1, savedOwnerDto1.getId());
        List<ItemRequest> afterSave = query.getResultList();

        assertEquals(1, afterSave.size());
        assertEquals(savedItemRequest.getId(), afterSave.get(0).getId());
        assertEquals(savedItemRequest.getCreated(), afterSave.get(0).getCreated());
        assertEquals(savedItemRequest.getDescription(), afterSave.get(0).getDescription());
    }

    @Test
    void addItemRequest_whenRequesterIdIsNull_returnIdNotFoundException() {
        Long requesterId = 1001L;
        assertThrows(IdNotFoundException.class,
                () -> itemRequestService.addItemRequest(itemRequestDto1, requesterId));
    }

    @Test
    void addItemRequest_whenRequesterNotFound_returnValidateException() {
        Long requesterId = 1001L;
        assertThrows(IdNotFoundException.class,
                () -> itemRequestService.addItemRequest(itemRequestDto1, requesterId));
    }


    @Test
    void getItemRequestsByUserId() {
        UserDto savedUserDto = userService.createUser(requesterDto1);
        ItemRequestOutputDto savedItemRequest =
                itemRequestService.addItemRequest(itemRequestDto1, savedUserDto.getId());

        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);

        List<ItemRequestWithAnswersDto> itemsFromDb =
                itemRequestService.getItemRequestsByUserId(savedUserDto.getId());

        assertEquals(1, itemsFromDb.size());

        assertEquals(savedItemRequest.getId(), itemsFromDb.get(0).getId());
        assertEquals(savedItemRequest.getCreated(), itemsFromDb.get(0).getCreated());
        assertEquals(itemRequestDto1.getDescription(), itemsFromDb.get(0).getDescription());
    }

    @Test
    void getItemRequestsByUserId_whenUserNotFound_returnNotFoundRecordInDb() {
        Long requesterId = 1001L;
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> itemRequestService.getItemRequestsByUserId(requesterId));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void getAllRequestForSee_WhenAllIsOk() {
        UserDto savedRequesterDto = userService.createUser(requesterDto1);
        UserDto savedOwnerDto = userService.createUser(ownerDto1);

        ItemRequestOutputDto savedItemRequest =
                itemRequestService.addItemRequest(itemRequestDto1, savedRequesterDto.getId());

        query = em.createQuery("Select ir from ItemRequest ir where ir.requester.id <> :userId", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.setParameter("userId", savedOwnerDto.getId())
                .getResultList();
        System.out.println("Для проверки запросов в БД. itemRequestList: size = " + itemRequestList.size()
                + "||\t\t\"" + itemRequestList.get(0).getDescription() + "\".");

        List<ItemRequestWithAnswersDto> emptyItemsFromDbForRequester =
                itemRequestService.getAllRequests(savedRequesterDto.getId(), 0, 5);

        assertEquals(0, emptyItemsFromDbForRequester.size());

        List<ItemRequestWithAnswersDto> oneItemFromDbForOwner =
                itemRequestService.getAllRequests(savedOwnerDto.getId(), 0, 1);

        assertEquals(savedItemRequest.getId(), oneItemFromDbForOwner.get(0).getId());
        assertEquals(savedItemRequest.getDescription(), oneItemFromDbForOwner.get(0).getDescription());
        assertTrue(oneItemFromDbForOwner.get(0).getItems().isEmpty());
        assertEquals(savedItemRequest.getCreated(), oneItemFromDbForOwner.get(0).getCreated());
    }

    @Test
    void getAllRequestForSee_whenRequesterNotFound_returnNotFoundRecordInDb() {
        Long requesterId = 1001L;
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> itemRequestService.getAllRequests(requesterId, 0, 5));
        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void getItemRequestById_whenAllIsOk_returnItemRequestDtoWithAnswers() {
        UserDto savedRequesterDto = userService.createUser(requesterDto1);
        UserDto savedOwnerDto = userService.createUser(ownerDto1);
        UserDto observer = userService.createUser(UserDto.builder().name("nablyudatel").email("1@re.hg").build());

        ItemRequestOutputDto savedItRequest =
                itemRequestService.addItemRequest(itemRequestDto1, savedRequesterDto.getId());


        ItemRequestWithAnswersDto itRequestDtoFromDbObserver =
                itemRequestService.getItemRequestById(observer.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itRequestDtoFromDbObserver.getId());
        assertEquals(savedItRequest.getCreated(), itRequestDtoFromDbObserver.getCreated());
        assertEquals(savedItRequest.getDescription(), itRequestDtoFromDbObserver.getDescription());

        ItemRequestWithAnswersDto itemRequestDtoWithAnswerForOwner =
                itemRequestService.getItemRequestById(savedOwnerDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itemRequestDtoWithAnswerForOwner.getId());
        assertEquals(savedItRequest.getCreated(), itemRequestDtoWithAnswerForOwner.getCreated());
        assertEquals(savedItRequest.getDescription(), itemRequestDtoWithAnswerForOwner.getDescription());

        ItemRequestWithAnswersDto itReqDtoWithAnswerForRequester =
                itemRequestService.getItemRequestById(savedRequesterDto.getId(), savedItRequest.getId());

        assertEquals(savedItRequest.getId(), itReqDtoWithAnswerForRequester.getId());
        assertEquals(savedItRequest.getCreated(), itReqDtoWithAnswerForRequester.getCreated());
        assertEquals(savedItRequest.getDescription(), itReqDtoWithAnswerForRequester.getDescription());
    }

    @Test
    void getItemRequestById_whenRequestNotFound_returnIdNotFoundException() {
        UserDto savedRequesterDto = userService.createUser(requesterDto1);
        Long requestId = 1001L;
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> itemRequestService.getItemRequestById(savedRequesterDto.getId(), requestId));
        assertEquals("Запрос не найден",
                ex.getMessage());
    }

    @Test
    void getItemRequestById_whenUserNotFound_returnIdNotFoundException() {
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1001L, 1L));
        assertEquals("Пользователь не найден",
                ex.getMessage());
    }
}
