package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService userService;
    private final ModelMapper userMapper;
    UserDto userDto1;
    User user1;
    UserDto userDto2;
    User user2;
    User userNull;
    UserDto userDtoNull;
    User userAllFieldsNull;
    UserDto userDtoAllFieldsNull;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("name1");
        user1.setEmail("email1@yandex.ru");

        userDto1 = new UserDto();
        userDto1.setId(user1.getId());
        userDto1.setName(user1.getName());
        userDto1.setEmail(user1.getEmail());

        user2 = new User();
        user2.setId(2L);
        user2.setName("name2");
        user2.setEmail("email2@yandex.ru");

        userDto2 = new UserDto();
        userDto2.setId(user2.getId());
        userDto2.setName(user2.getName());
        userDto2.setEmail(user2.getEmail());

        userAllFieldsNull = new User();
        userDtoAllFieldsNull = new UserDto();

        userNull = null;
        userDtoNull = null;
    }

    @Test
    void getUserById_WhenAllIsOk() {
        UserDto savedUser = userService.createUser(userDto1);

        UserDto user = userService.getUserById(savedUser.getId());

        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto1.getName());
        assertEquals(user.getEmail(), userDto1.getEmail());
    }

    @Test
    void getUserById_whenUserNotFoundInDb_return() {
        UserDto savedUser = userService.createUser(userDto1);

        assertThrows(IdNotFoundException.class,
                () -> userService.getUserById(100L));
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        List<UserDto> userDtos = List.of(userDto1, userDto2);

        userService.createUser(userDto1);
        userService.createUser(userDto2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(userDtos.size(), result.size());
        for (UserDto user : userDtos) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Test
    void addToStorage() {
        userService.createUser(userDto1);

        List<UserDto> users = userService.getAllUsers();
        boolean result = false;
        Long id = users.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);

        UserDto userDtoFromDb = userService.getUserById(id);

        assertEquals(1, users.size());
        assertEquals(userDto1.getName(), userDtoFromDb.getName());
        assertEquals(userDto1.getEmail(), userDtoFromDb.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOkAndNameIsNull_returnUpdatedUser() {
        UserDto createdUser = userService.createUser(userDto1);

        List<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userDto2.setId(createdUser.getId());
        userDto2.setName(null);
        userService.updateUser(userDto2, userDto2.getId());

        UserDto userDtoFromDbAfterUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto2.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOkAndEmailIsNull_returnUpdatedUser() {
        UserDto createdUser = userService.createUser(userDto1);

        List<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userDto2.setId(createdUser.getId());
        userDto2.setEmail(null);
        userService.updateUser(userDto2, userDto2.getId());

        UserDto userDtoFromDbAfterUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto2.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto1.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOk_returnUpdatedUser() {
        UserDto createdUser = userService.createUser(userDto1);

        List<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userDto2.setId(createdUser.getId());
        userService.updateUser(userDto2, userDto2.getId());

        UserDto userDtoFromDbAfterUpdate = userService.getUserById(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto2.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto2.getEmail());
    }

    @Test
    void updateInStorage_whenUserNotFound_returnNotFoundRecordInBD() {
        userDto1.setId(100L);
        IdNotFoundException ex = assertThrows(IdNotFoundException.class,
                () -> userService.updateUser(userDto1, userDto1.getId()));
    }

    @Test
    void removeFromStorage() {
        UserDto savedUser = userService.createUser(userDto1);
        List<UserDto> beforeDelete = userService.getAllUsers();
        assertEquals(1, beforeDelete.size());
        userService.deleteUser(savedUser.getId());
        List<UserDto> afterDelete = userService.getAllUsers();
        assertEquals(0, afterDelete.size());
    }

    @Test
    void userMapperTest_mapToModel_whenAllIsOk() {
        User user1 = userMapper.map(userDto1, User.class);
        assertEquals(userDto1.getId(), user1.getId());
        assertEquals(userDto1.getName(), user1.getName());
        assertEquals(userDto1.getEmail(), user1.getEmail());
    }

    @Test
    void userMapperTest_mapToModel_whenAllFieldsAreNull() {
        User userNull = userMapper.map(userDtoAllFieldsNull, User.class);
        assertEquals(userDtoAllFieldsNull.getId(), userNull.getId());
        assertEquals(userDtoAllFieldsNull.getName(), userNull.getName());
        assertEquals(userDtoAllFieldsNull.getEmail(), userNull.getEmail());
    }

    @Test
    void userMapperTest_mapToModel_whenDtoIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> userMapper.map(userDtoNull, User.class));
    }

    @Test
    void userMapperTest_mapToDto_whenAllIsOk() {
        UserDto userDto1 = userMapper.map(user1, UserDto.class);
        assertEquals(user1.getId(), userDto1.getId());
        assertEquals(user1.getName(), userDto1.getName());
        assertEquals(user1.getEmail(), userDto1.getEmail());
    }

    @Test
    void userMapperTest_mapToDto_whenAllFieldsAreNull() {
        UserDto userDtoNull = userMapper.map(userAllFieldsNull, UserDto.class);
        assertEquals(userAllFieldsNull.getId(), userDtoNull.getId());
        assertEquals(userAllFieldsNull.getName(), userDtoNull.getName());
        assertEquals(userAllFieldsNull.getEmail(), userDtoNull.getEmail());
    }

    @Test
    void userMapperTest_mapToDto_whenModelIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> userMapper.map(userNull, UserDto.class));
    }
}
