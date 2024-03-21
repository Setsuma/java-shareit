package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    @Autowired
    private JacksonTester<BookingOutputDto> jsonOut;

    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
    }

    @Test
    void serializeBookingOutputDtoTest() throws Exception {
        BookingOutputDto bookingOutputDto = BookingOutputDto.builder()
                .id(1)
                .start(LocalDateTime.parse("2024-01-21T10:10:10"))
                .end(LocalDateTime.parse("2024-01-22T10:10:10"))
                .status(BookingStatus.WAITING)
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item desc")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@yandex.com")
                                .build())
                        .request(null)
                        .build())
                .booker(User.builder()
                        .id(2L)
                        .name("booker")
                        .email("booker@yandex.com")
                        .build())
                .build();

        assertThat(this.jsonOut.write(bookingOutputDto)).isEqualToJson(new ClassPathResource("appearanceBookingOutputDto.json"));
    }

    @Test
    void deserializeBookingDtoTest() throws Exception {
        String content = "{\"itemId\":\"1\",\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
        assertThat(this.json.parse(content)).isEqualTo(
                BookingDto.builder().itemId(1).start(start).end(end).build());
        assertThat(this.json.parseObject(content).getItemId()).isEqualTo(1);
        assertThat(this.json.parseObject(content).getStart()).isEqualTo(start);
        assertThat(this.json.parseObject(content).getEnd()).isEqualTo(end);
    }
}