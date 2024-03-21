package ru.practicum.shareit.mapper;

import com.github.rozidan.springboot.modelmapper.ConverterConfigurer;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingToBookingForItemDtoConverter extends ConverterConfigurer<Booking, BookingForItemDto> {
    @Override
    public Converter<Booking, BookingForItemDto> converter() {
        return new AbstractConverter<Booking, BookingForItemDto>() {
            @Override
            protected BookingForItemDto convert(Booking source) {
                BookingForItemDto destination = BookingForItemDto.builder()
                        .id(source.getId())
                        .start(source.getStart())
                        .end(source.getEnd())
                        .bookerId(source.getBooker().getId())
                        .status(source.getStatus())
                        .build();
                return destination;
            }
        };
    }
}