package ru.practicum.shareit.mapper;

import com.github.rozidan.springboot.modelmapper.ConverterConfigurer;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingToBookingDtoConverter extends ConverterConfigurer<Booking, BookingDto> {
    @Override
    public Converter<Booking, BookingDto> converter() {
        return new AbstractConverter<Booking, BookingDto>() {
            @Override
            protected BookingDto convert(Booking source) {
                BookingDto destination = new BookingDto();
                destination.setItemId(source.getItem().getId());
                destination.setStart(source.getStart());
                destination.setEnd(source.getEnd());
                return destination;
            }
        };
    }
}
