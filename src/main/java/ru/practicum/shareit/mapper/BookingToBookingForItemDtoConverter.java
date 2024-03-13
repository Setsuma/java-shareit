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
                BookingForItemDto destination = new BookingForItemDto();
                destination.setId(source.getId());
                destination.setStart(source.getStart());
                destination.setEnd(source.getEnd());
                destination.setBookerId(source.getBooker().getId());
                destination.setStatus(source.getStatus());
                return destination;
            }
        };
    }
}